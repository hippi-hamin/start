package com.finalpro.start.util;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalpro.start.dto.PlaceDTO;
import com.finalpro.start.util.kakaoUtil.Document;
import com.finalpro.start.util.kakaoUtil.KakaoAddress;
import com.finalpro.start.util.kakaoUtil.KakaoDirections;
import com.finalpro.start.util.kakaoUtil.KakaoDirections.Route;
import com.finalpro.start.util.kakaoUtil.KakaoDirections.Route.Section;
import com.finalpro.start.util.kakaoUtil.KakaoDirections.Route.Section.Road;

public class KakaoApiUtil {

    private static final String REST_API_KEY = "448274e35c1f29a434d87a9242405056";

    public static List<PlaceDTO> getPlaceByKeyWord(String keyword, PlaceDTO center)
            throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        String url = "https://dapi.kakao.com/v2/local/search/keyword.JSON";
        url += "?query=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        url += "&x=" + URLEncoder.encode(center.getX().toString(), StandardCharsets.UTF_8);
        url += "&y=" + URLEncoder.encode(center.getY().toString(), StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder().header("Authorization", "KakaoAK " + REST_API_KEY)
                .uri(URI.create(url)).GET().build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        KakaoAddress kakaoAddress = new ObjectMapper().readValue(responseBody, KakaoAddress.class);
        List<Document> documents = kakaoAddress.getDocuments();

        if (documents == null || documents.isEmpty()) {
            System.out.print("documents is null or empty");
            return null;
        }

        List<PlaceDTO> placeList = new ArrayList<>();
        for (Document document : documents) {
            PlaceDTO placeDto = new PlaceDTO(document.getX(), document.getY());
            placeList.add(placeDto);
        }
        return placeList;
    }

    public static List<PlaceDTO> getVehiclePaths(PlaceDTO from, PlaceDTO to, List<PlaceDTO> waypoints, String priority)
            throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        StringBuilder url = new StringBuilder("https://apis-navi.kakaomobility.com/v1/directions");

        url.append("?origin=").append(URLEncoder.encode(from.getX() + "," + from.getY(), StandardCharsets.UTF_8));
        url.append("&destination=").append(URLEncoder.encode(to.getX() + "," + to.getY(), StandardCharsets.UTF_8));
        url.append("&priority=").append(URLEncoder.encode(priority, StandardCharsets.UTF_8));

        if (waypoints != null && !waypoints.isEmpty()) {
            String waypointsParam = waypoints.stream()
                    .map(wp -> wp.getX() + "," + wp.getY())
                    .collect(Collectors.joining("|"));
            // URL 인코딩 처리
            waypointsParam = URLEncoder.encode(waypointsParam, StandardCharsets.UTF_8.toString());
            url.append("&waypoints=").append(waypointsParam);
        }

        // 요청 URL을 로깅하여 확인
        System.out.println("Request URL: " + url.toString());

        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "KakaoAK " + REST_API_KEY)
                .header("Content-Type", "application/json")
                .uri(URI.create(url.toString()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        // 응답 본문을 로깅하여 확인
        System.out.println("Response Body: " + responseBody);

        if (response.statusCode() != 200) {
            throw new IOException("HTTP error code : " + response.statusCode());
        }

        // JSON 응답 확인
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoDirections kakaoDirections;
        try {
            kakaoDirections = objectMapper.readValue(responseBody, KakaoDirections.class);
        } catch (Exception e) {
            System.err.println("Failed to parse response: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // 빈 리스트 반환
        }

        List<PlaceDTO> placeList = new ArrayList<>();

        List<KakaoDirections.Route> routes = kakaoDirections.getRoutes();
        for (KakaoDirections.Route route : routes) {
            List<KakaoDirections.Route.Section> sections = route.getSections();
            for (KakaoDirections.Route.Section section : sections) {
                List<KakaoDirections.Route.Section.Road> roads = section.getRoads();
                for (KakaoDirections.Route.Section.Road road : roads) {
                    List<Double> vertexes = road.getVertexes();
                    for (int i = 0; i < vertexes.size(); i += 2) { // i를 2씩 증가시킴
                        placeList.add(new PlaceDTO(vertexes.get(i + 1), vertexes.get(i)));
                    }
                }
            }
        }

        return placeList;
    }


    public static PlaceDTO getPointByAddress(String address) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String url = "https://dapi.kakao.com/v2/local/search/address.json";
        url += "?query=" + URLEncoder.encode(address, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()//
                .header("Authorization", "KakaoAK " + REST_API_KEY)//
                .uri(URI.create(url))//
                .GET()//
                .build();

        System.out.println(request.headers());

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        System.out.println(responseBody);

        KakaoAddress kakaoAddress = new ObjectMapper().readValue(responseBody, KakaoAddress.class);
        List<Document> documents = kakaoAddress.getDocuments();
        if (documents.isEmpty()) {
            return null;
        }
        Document document = documents.get(0);
        PlaceDTO placeDTO = new PlaceDTO();
        placeDTO.setX(document.getX());
        placeDTO.setY(document.getY());
        return placeDTO;
    }
}
