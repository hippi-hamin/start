package com.finalpro.start.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
	// 체크박스 체크 시 해당 지역 리스트(지역) -안재문-
	@GetMapping("/searchByRegion")
	public List<PlaceDTO> searchByRegion(@RequestParam(value = "regions", required = false) List<String> regions) {
		return placeService.searchByRegion(regions);
	}
	
	// 체크박스 선택 시 해당 지역 리스트(테마) -안재문-
	@GetMapping("/searchByTheme")
	public List<PlaceDTO> searchByTheme(@RequestParam(value="themes", required = false) List<String> themes){
		return placeService.searchByTheme(themes);
	}
	
	@GetMapping("/searchByFilters")
	public List<PlaceDTO> searchByFilters(
	        @RequestParam(value = "themes", required = false) List<String> themes,
	        @RequestParam(value = "regions", required = false) List<String> regions) {
	    // 로그 추가
	    log.info("Received themes: " + themes);
	    log.info("Received regions :" + regions);
	    return placeService.searchByFilters(themes, regions);
	}


}
