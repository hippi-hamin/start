package com.finalpro.start.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finalpro.start.dto.PlaceDTO;
import com.finalpro.start.service.PlaceService;

@RestController
public class PlaceRestController {
	
	@Autowired
	private PlaceService placeService;
	@PostMapping("/savaPlace")
	public String savePlace(@RequestParam("s_name") String s_name,
            				@RequestParam("s_location") String s_location) {
		
		// 파라미터들을 새로운 PlaceDTO 객체에 설정
		PlaceDTO place = new PlaceDTO();
		place.setP_name(s_name);
        place.setP_location(s_location);
		
        // PlaceService로 전달 
        placeService.savePlace(place);
        
       return "redirect:placeListByLocation";
	}
}
