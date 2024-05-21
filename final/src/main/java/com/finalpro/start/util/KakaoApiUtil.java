package com.finalpro.start.util;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    
 // 시간이랑 거리 넣는거 때문에 DTO에서 MAP으로 바꿈
    public static Map<String, Object> getVehiclePaths(PlaceDTO from, PlaceDTO to, List<PlaceDTO> waypoints, String priority)
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
            waypointsParam = URLEncoder.encode(waypointsParam, StandardCharsets.UTF_8.toString());
            url.append("&waypoints=").append(waypointsParam);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "KakaoAK " + REST_API_KEY)
                .header("Content-Type", "application/json")
                .uri(URI.create(url.toString()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        if (response.statusCode() != 200) {
            throw new IOException("HTTP error code : " + response.statusCode());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoDirections kakaoDirections = objectMapper.readValue(responseBody, KakaoDirections.class);

        Map<String, Object> resultMap = new HashMap<>();
        List<PlaceDTO> placeList = new ArrayList<>();

        List<KakaoDirections.Route> routes = kakaoDirections.getRoutes();
        if (!routes.isEmpty()) {
            KakaoDirections.Route.Summary summary = routes.get(0).getSummary();
            resultMap.put("distance", summary.getDistance());
            resultMap.put("duration", summary.getDuration());

            List<KakaoDirections.Route.Section> sections = routes.get(0).getSections();
            for (KakaoDirections.Route.Section section : sections) {
                List<KakaoDirections.Route.Section.Road> roads = section.getRoads();
                for (KakaoDirections.Route.Section.Road road : roads) {
                    List<Double> vertexes = road.getVertexes();
                    for (int i = 0; i < vertexes.size(); i += 2) {
                        placeList.add(new PlaceDTO(vertexes.get(i + 1), vertexes.get(i)));
                    }
                }
            }
        }

        resultMap.put("placeList", placeList);
        return resultMap;
    }

    
    // 장소 등록할 때 주소를 좌표로 바꿔주는 메소
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
