package com.finalpro.start.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalpro.start.dto.PlaceDTO;
import com.finalpro.start.service.PlaceService;
import com.finalpro.start.util.KakaoApiUtil;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class PlaceController {

	@Autowired
	private PlaceService placeService;

	// 장소 리스트 페이지 이동
	@GetMapping("placeList")
	public String placeList(Model model,
			@RequestParam(value = "p_location", required = false, defaultValue = "defaultLocation") String p_location,
			@RequestParam(value = "p_thema", required = false, defaultValue = "defaultThema") String p_thema) {
		log.info("placeList()");
		List<PlaceDTO> placeList = placeService.getPlaceList(p_location, p_thema);
		model.addAttribute("placeList", placeList);
		return "placeList";
	}

	@GetMapping("placeListByLocation")
	public String placeListByLocation(@RequestParam(value = "p_location", required = false) String p_location,
			Model model) {
		String view = null;
		List<PlaceDTO> place;

		if (p_location != null && !p_location.isEmpty()) {
			place = placeService.placeListByLocation(p_location);
		} else {
			place = placeService.getPlaceList();
		}

		model.addAttribute("placeListByLocation", place);
		view = "placeListByLocation";

		return view;
	}

	@GetMapping("placeListByTheme")
	public String placeByTheme(@RequestParam(value = "p_thema", required = false) String p_thema, Model model) {
		String view = null;
		List<PlaceDTO> place;

		if (p_thema != null && !p_thema.isEmpty()) {
			place = placeService.placeListByTheme(p_thema);
		} else {
			place = placeService.getPlaceList();
		}

		model.addAttribute("placeListByTheme", place);
		view = "placeListByTheme";

		return view;
	}

	// placeDetail
	@GetMapping("/placeDetail/{p_id}")
	public String showPlaceDetail(@PathVariable("p_id") int p_id, Model model) {
		// 조회수 증가 전에 해당 장소의 정보를 가져옴
		PlaceDTO place = placeService.findById(p_id);
		// 조회수 증가
		placeService.increaseViews(p_id);
		// 가져온 정보를 모델에 추가하여 템플릿으로 전달
		model.addAttribute("place", place);

		// 장소 세부 정보를 보여줄 템플릿의 이름을 반환
		return "placeDetail";
	}

	@GetMapping("/getImage/{imageName}")
	public ResponseEntity<byte[]> getImage(@PathVariable String imageName, HttpSession session) {
		try {
			// 이미지 파일의 경로를 설정합니다.
			String uploadDirectory = "/Users/upLoad/"; // 업로드된 이미지 파일이 있는 경로

			Path imagePath = Paths.get(uploadDirectory, imageName);

			// 이미지 파일을 읽어와 byte 배열로 변환합니다.
			byte[] imageBytes = Files.readAllBytes(imagePath);

			// HTTP 응답에 이미지와 적절한 Content-Type을 설정하여 반환합니다.
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
		} catch (IOException e) {
			// 이미지 파일을 읽어오는 도중에 예외가 발생한 경우
			e.printStackTrace();
			// 이미지를 찾을 수 없을 때는 HTTP 상태 코드 404를 반환합니다.
			return ResponseEntity.notFound().build();
		}

	}

	@GetMapping("upLoadPlace")
	public String upLoadPlace() {
		log.info("upLoad()");
		return "upLoadPlace";
	}

	@PostMapping("upLoadPlaceProc")
	public String upLoadPlaceProc(@RequestParam("files") List<MultipartFile> files,
			@RequestParam("address") String address, // 주소 파라미터 추가
			HttpSession session, @Validated @ModelAttribute("PlaceDTO") PlaceDTO placeDTO, BindingResult bindingResult,
			RedirectAttributes rttr) {

		log.info("upLoadPlaceProc()");
		log.info("PlaceDTO {}:", placeDTO);

		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach(error -> log.error(error.getDefaultMessage()));
			rttr.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:upLoadPlace";
		}

		try {
			// 주소로부터 좌표 추출
			PlaceDTO optionalCoords = KakaoApiUtil.getPointByAddress(address);
			if (optionalCoords != null) {
				placeDTO.setX(optionalCoords.getX()); // 좌표 설정
				placeDTO.setY(optionalCoords.getY());
			} else {
				rttr.addFlashAttribute("errorMessage", "주소로부터 좌표를 찾을 수 없습니다.");
				return "redirect:upLoadPlace";
			}

			// 파일 업로드 및 장소 정보 저장
			String view = placeService.upLoadPlaceProc(files, session, placeDTO, rttr);
			return view;
		} catch (Exception e) {
			log.error("Error processing upload: ", e);
			rttr.addFlashAttribute("errorMessage",
					"Error processing your upload. Please try again or contact support.");
			return "redirect:upLoadPlace";
		}
	}

	@PostMapping("/addPlaceToCart")
	public String addPlaceToCart(@RequestParam("p_id") String p_idStr, HttpSession session) {
		 log.info("Received p_id: {}", p_idStr); // 로그 추가
		try {
	        int p_id = Integer.parseInt(p_idStr);
	        List<PlaceDTO> cart = (List<PlaceDTO>) session.getAttribute("cart");

	        if (cart == null) {
	            cart = new ArrayList<>();
	            session.setAttribute("cart", cart);
	        }

	        PlaceDTO place = placeService.findById(p_id);
	        if (place != null && !cart.contains(place)) {
	            cart.add(place);
	            return "redirect:/placeList";
	        } else {
	            return "redirect:/error";
	        }
	    } catch (NumberFormatException e) {
	        return "redirect:/error";
	    }
	}


	@GetMapping("/showCart")
	public String showCart(Model model, HttpSession session) {
		List<PlaceDTO> cart = (List<PlaceDTO>) session.getAttribute("cart");
		model.addAttribute("cart", cart);
		return "cartPage"; // 장바구니 페이지
	}

	@GetMapping("mapPaths") // url : /map/paths
	public String getMapPaths(@RequestParam(name = "fromAddress", required = false) String fromAddress,
			@RequestParam(name = "wayAddress", required = false) String wayAddress,
			@RequestParam(name = "priority", required = false) String priority, //
			@RequestParam(name = "toAddress", required = false) String toAddress, Model model)
			throws IOException, InterruptedException {
		PlaceDTO fromPoint = null;
		PlaceDTO wayPoint = null;
		PlaceDTO toPoint = null;
		if (fromAddress != null && !fromAddress.isEmpty()) {
			fromPoint = KakaoApiUtil.getPointByAddress(fromAddress);
			model.addAttribute("fromPoint", fromPoint);
		}
		if (toAddress != null && !toAddress.isEmpty()) {
			toPoint = KakaoApiUtil.getPointByAddress(toAddress);
			log.info("toAddress" + toAddress);
			model.addAttribute("toPoint", toPoint);
		}

		if (wayAddress != null && !wayAddress.isEmpty()) {
			wayPoint = KakaoApiUtil.getPointByAddress(wayAddress);
			model.addAttribute("wayPoint", wayPoint);
		}

		if (fromPoint != null && toPoint != null || wayPoint != null) {
			List<PlaceDTO> placeList = KakaoApiUtil.getVehiclePaths(fromPoint, toPoint, wayPoint, null);
			log.info("wayPoint" + wayPoint);
			String placeListJson = new ObjectMapper().writer().writeValueAsString(placeList);
			model.addAttribute("placeList", placeListJson);
			log.info("placelist" + placeListJson);

		}
		return "mapPaths";
	}

	@GetMapping("map")
	public String getMethodName() {
		return "map";
	}
}
