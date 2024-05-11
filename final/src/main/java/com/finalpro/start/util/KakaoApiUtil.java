package com.finalpro.start.util;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

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
		url += "?query=" + URLEncoder.encode(keyword, "UTF-8");
		url += "&x=" + center.getX();
		url += "&y=" + center.getY();
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

		// 이후의 코드는 documents가 null이 아니고 비어있지 않은 경우에만 실행됩니다.

		List<PlaceDTO> placeList = new ArrayList<>();
		for (Document document : documents) {
			PlaceDTO placeDto = new PlaceDTO(document.getX(), document.getY());
			placeList.add(placeDto);

		}
		return placeList;
	}

	public static List<PlaceDTO> getVehiclePaths(PlaceDTO from, PlaceDTO to, PlaceDTO waypoint, String priority)
	        throws IOException, InterruptedException {

	    HttpClient client = HttpClient.newHttpClient();
	    String url = "https://apis-navi.kakaomobility.com/v1/directions";

	    url += "?origin=" + from.getX() + "," + from.getY();
	    url += "priority=" + "DISTANCE";
	    if(waypoint != null) {
	        url += "&waypoints=" + waypoint.getX() + "," + waypoint.getY();
	    }
	    url += "&destination=" + to.getX() + "," + to.getY();

	    HttpRequest request = HttpRequest.newBuilder()
	            .header("Authorization", "KakaoAK " + REST_API_KEY)
	            .header("Content-Type", "application/json")
	            .uri(URI.create(url))
	            .GET()
	            .build();
	    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	    String responseBody = response.body();

	    List<PlaceDTO> placeList = new ArrayList<PlaceDTO>();

	    KakaoDirections kakaoDirections = new ObjectMapper().readValue(responseBody, KakaoDirections.class);
	    List<Route> routes = kakaoDirections.getRoutes();
	    for (Route route : routes) {
	        List<Section> sections = route.getSections();
	        for (Section section : sections) {
	            List<Road> roads = section.getRoads();
	            for (Road road : roads) {
	                List<Double> vertexes = road.getVertexes();
	                for (int i = 0; i < vertexes.size(); i++) {
	                    placeList.add(new PlaceDTO(vertexes.get(i), vertexes.get(++i)));
	                }
	            }
	        }
	    }
	    return placeList;
	}




	public static PlaceDTO getPointByAddress(String address) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String url = "https://dapi.kakao.com/v2/local/search/address.json";
		url += "?query=" + URLEncoder.encode(address, "UTF-8");
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
