package com.finalpro.start.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalpro.start.dto.PlaceDTO;
import com.finalpro.start.service.PlaceService;
import com.finalpro.start.util.KakaoApiUtil;

import jakarta.servlet.http.HttpSession;

@Controller
@Slf4j
public class PlaceController {

	@Autowired
	private PlaceService placeService;
	
	@GetMapping("/placeList")
	String placeList () {
		
		return "placeList";
	}
	
	@GetMapping("upLoadPlace")
	public String upLoadPlace() {
		log.info("upLoad()");
		return "upLoadPlace";
	}

	@PostMapping("upLoadPlaceProc")
	public String upLoadPlaceProc(@RequestParam("files") List<MultipartFile> files, HttpSession session,
			@ModelAttribute("PlaceDTO") PlaceDTO placeDTO, RedirectAttributes rttr) {
		
		log.info("upLoadPlaceProc()");
		log.info("placeDTO {}:", placeDTO);
		try {
			String view = placeService.upLoadPlaceProc(files, session, placeDTO, rttr);
			return view;
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:upLoadPlace";
		}
	}
	
	@GetMapping("searchRoad")
	public String getPlacePath(
							   @RequestParam (name="x", required = false) Double x,
							   @RequestParam (name="y", required = false) Double y,
							   @RequestParam (name="keyword", required = false) String keyword,
							   Model model) throws IOException, InterruptedException {
		if(x != null && y != null && keyword != null) {
			List<PlaceDTO> keywordPlaceList = KakaoApiUtil.getPlaceByKeyWord(keyword, new PlaceDTO(x, y));
			String keywordPlaceListJson = new ObjectMapper().writer().writeValueAsString(keywordPlaceList);
			model.addAttribute("keywordPlacetList", keywordPlaceListJson);
		
			List<PlaceDTO> pathPlaceList = new ArrayList<>();
			for (int i=1; i<keywordPlaceList.size(); i++){
				PlaceDTO prevPoint = keywordPlaceList.get(i-1);
				PlaceDTO nextPoint = keywordPlaceList.get(i);
				pathPlaceList.addAll(KakaoApiUtil.getVehiclePaths(prevPoint, nextPoint, null));
			}
			String pathPlaceListJson = new ObjectMapper().writer().writeValueAsString(pathPlaceList);
			model.addAttribute("pathPlaceList", pathPlaceListJson);
		}
		
		return "searchRoad";
	}
	
	@GetMapping("mapPaths") // url : /map/paths
	public String getMapPaths(@RequestParam(name = "fromAddress",required = false) String fromAddress, @RequestParam(name ="wayAddress", required = false) String wayAddress,//
			@RequestParam(name = "toAddress" ,required = false) String toAddress,
			Model model) throws IOException, InterruptedException {
		PlaceDTO fromPoint = null;
		PlaceDTO wayPoint = null;
		PlaceDTO toPoint = null;
//		PlaceDTO wayPoint = null;
		if (fromAddress != null && !fromAddress.isEmpty()) {
			fromPoint = KakaoApiUtil.getPointByAddress(fromAddress);
			model.addAttribute("fromPoint", fromPoint);
		}
		if (toAddress != null && !toAddress.isEmpty()) {
			toPoint = KakaoApiUtil.getPointByAddress(toAddress);
			log.info("toAddress" + toAddress);
			model.addAttribute("toPoint", toPoint);
		}
		
		if( wayAddress != null && !wayAddress.isEmpty()) {
		wayPoint = KakaoApiUtil.getPointByAddress(wayAddress);
		model.addAttribute("wayPoint", wayPoint);
		}

		if (fromPoint != null && toPoint != null || wayPoint !=null) {
			List<PlaceDTO> placeList = KakaoApiUtil.getVehiclePaths(fromPoint, toPoint, wayPoint);
			log.info("wayPoint" + wayPoint);
			String placeListJson = new ObjectMapper().writer().writeValueAsString(placeList);
			model.addAttribute("placeList", placeListJson);
			log.info("placelist" + placeListJson);
			
		}
		return "mapPaths";
	}
	
	@GetMapping("addressToPoint")
	public String getPointByAddress(@RequestParam(value = "address",required = false) String address,
									Model model) throws IOException, InterruptedException {
		
		if( address!=null && !address.isEmpty()) {
			
			PlaceDTO placePoint = KakaoApiUtil.getPointByAddress(address);
			model.addAttribute("placePoint", placePoint);
			
		}
		
		return "addressToPoint";
	}
	
	@GetMapping("map")
	public String getMethodName() {
		return "map";
	}
}
