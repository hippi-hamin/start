package com.finalpro.start.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finalpro.start.dto.PlaceDTO;
import com.finalpro.start.service.PlaceService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class PlaceRestController {

	@Autowired
	private PlaceService placeService;

	@PostMapping("/savaPlace")
	public String savePlace(@RequestParam("s_name") String s_name, @RequestParam("s_location") String s_location) {

		// 파라미터들을 새로운 PlaceDTO 객체에 설정
		PlaceDTO place = new PlaceDTO();
		place.setP_name(s_name);
		place.setP_location(s_location);

		// PlaceService로 전달
		placeService.savePlace(place);

		return "redirect:placeListByLocation";
	}

	// make plan filter
	@GetMapping("/searchByFilters")
	public List<PlaceDTO> searchByFilters(@RequestParam(value = "themes", required = false) List<String> themes,
			@RequestParam(value = "mainRegions", required = false) List<String> mainRegions,
			@RequestParam(value = "subregions", required = false) List<String> subregions) {
		log.info("Received themes: " + themes);
		log.info("Received mainRegions: " + mainRegions);
		log.info("Received subregions: " + subregions);

		if ((themes == null || themes.isEmpty()) && (mainRegions == null || mainRegions.isEmpty())
				&& (subregions == null || subregions.isEmpty())) {
			return placeService.getPlaceList(null, null);
		}

		return placeService.searchByFilters(themes, mainRegions, subregions);
	}


	// 지역별 리스트
	@GetMapping("/fetchPlacesByLocation")
	public List<PlaceDTO> fetchPlacesLocation(@RequestParam(value = "location", required = false) String location) {
		log.info(location);
		return placeService.fetchPlacesLocation(location);
	}

	// 테마별 리스트
	@GetMapping("/fetchPlacesByTheme")
	public List<PlaceDTO> fetchPlacesTheme(@RequestParam(value = "theme", required = false) String theme) {
		log.info(theme);
		return placeService.fetchPlacesTheme(theme);
	}
	
	// 키워드로 검색
	@GetMapping("/searchByKeyword")
	public List<PlaceDTO> searchByKeyword(@RequestParam(value = "query", required = true) String query) {
		log.info("searchByKeyword()");
		return placeService.searchByKeyword(query);
	}
}
