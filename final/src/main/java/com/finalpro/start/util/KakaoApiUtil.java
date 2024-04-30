package com.finalpro.start.util;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalpro.start.dto.PlaceDTO;
import com.finalpro.start.util.kakaoUtil.Document;
import com.finalpro.start.util.kakaoUtil.KakaoAddress;
import com.finalpro.start.util.kakaoUtil.KakaoDirections;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	public static List<PlaceDTO> getVehiclePaths(PlaceDTO from, PlaceDTO to, List<PlaceDTO> waypoints)
			throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String url = "https://apis-navi.kakaomobility.com/v1/directions";
		url += "?origin=" + from.getX() + "," + from.getY() + "," + from.getP_name();

		if (waypoints != null && !waypoints.isEmpty()) {
			for (PlaceDTO waypoint : waypoints) {
				url += "&waypoint=" + waypoint.getX() + "," + waypoint.getY() + "," + waypoint.getP_name();
			}
		}
		url += "&destination=" + to.getX() + "," + to.getY() + "," + from.getP_name();

		HttpRequest request = HttpRequest.newBuilder().header("Authorization", "KakaoAK " + REST_API_KEY)
				.header("Content-Type", "application/json").uri(URI.create(url)).GET().build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		String responseBody = response.body();

		List<PlaceDTO> placeList = new ArrayList<>();

		try {
			KakaoDirections kakaoDirections = new ObjectMapper().readValue(responseBody, KakaoDirections.class);
			log.info(responseBody);
			if (kakaoDirections != null && kakaoDirections.getRoutes() != null
					&& !kakaoDirections.getRoutes().isEmpty()) {
				// 정상적으로 역직렬화된 경우
				// 이후 작업 수행
			} else {
				// 역직렬화가 실패하거나 반환된 데이터가 비어있는 경우
				System.out.println("Failed to deserialize KakaoDirections object or received empty data");
			}
		} catch (JsonProcessingException e) {
			// 역직렬화 과정에서 예외가 발생한 경우
			e.printStackTrace();
			System.out.println("Failed to deserialize JSON data: " + e.getMessage());
		}

		return placeList;
	}
}
