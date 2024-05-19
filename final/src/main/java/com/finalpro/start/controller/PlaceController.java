package com.finalpro.start.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.finalpro.start.service.PlatformService;
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
		if (p_location.equals("defaultLocation") && p_thema.equals("defaultThema")) {
			List<PlaceDTO> placeList = placeService.getPlaceList();
			model.addAttribute("placeList", placeList);
		} else {
			List<PlaceDTO> placeList = placeService.getPlaceList(p_location, p_thema);
			model.addAttribute("placeList", placeList);
		}

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
			// 실제 이미지 파일이 저장된 디렉터리 경로 설정
			String uploadDirectory = "/Users/upLoad/";

			// 이미지 파일의 경로 설정
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
				rttr.addFlashAttribute("msg", "주소로부터 좌표를 찾을 수 없습니다.");
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

	// 장소 수정 -안재문 
	@GetMapping("/updatePlace/{p_id}")
	public String updatePlace(@PathVariable("p_id") int p_id, Model model) {
		log.info("updatePlace()");

		// p_id에 해당하는 장소 정보 가져오기 (예시로 service 메서드 사용)
		PlaceDTO placeDTO = placeService.findById(p_id);

		// 모델에 장소 정보 추가
		model.addAttribute("place", placeDTO);

		return "updatePlace";
	}
	// 장소 수정 처리 -안재문 
	@PostMapping("/updatePlaceProc")
	public String updatePlaceProc(@RequestParam("files") List<MultipartFile> files, HttpSession session,
			@RequestParam("p_id") int p_id, @RequestParam("p_location") String p_location,
			@RequestParam("p_name") String p_name, @RequestParam("p_thema") String p_thema,
			@RequestParam("p_description") String p_description,
			@RequestParam("address") String address,
			RedirectAttributes rttr) {
		String view = null;

		log.info("updatePlaceProc(), controller");
		try {
			PlaceDTO placeDTO = new PlaceDTO();
			placeDTO.setP_id(p_id);
			placeDTO.setP_location(p_location);
			placeDTO.setP_name(p_name);
			placeDTO.setP_thema(p_thema);
			placeDTO.setP_description(p_description);
			
	        
			// 주소로부터 좌표 추출
	        PlaceDTO optionalCoords = KakaoApiUtil.getPointByAddress(address);
	        if (optionalCoords != null) {
	            placeDTO.setX(optionalCoords.getX()); // 좌표 설정
	            placeDTO.setY(optionalCoords.getY());
	        } else {
	            rttr.addFlashAttribute("msg", "주소로부터 좌표를 찾을 수 없습니다.");
	            return "redirect:updatePlace";
	        }
	        
	       

			view = placeService.updatePlaceProc(files, session, placeDTO, rttr);
			return view;
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:updatePlace";
		}
	}

	@PostMapping("/addPlaceToCart")
	public ResponseEntity<?> addPlaceToCart(@RequestParam("p_id") String p_idStr, HttpSession session) {
		log.info("addToCart()");
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
				return ResponseEntity.ok().body(cart); // 장바구니 아이템 리스트를 반환
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Item already in cart or not found");
			}
		} catch (NumberFormatException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid product ID");
		}
	}

	@GetMapping("/showCart")
	public ResponseEntity<List<PlaceDTO>> showCart(HttpSession session) {
		List<PlaceDTO> cart = (List<PlaceDTO>) session.getAttribute("cart");
		return ResponseEntity.ok(cart); // JSON 형태로 장바구니 목록 반환
	}

	@PostMapping("/clearCart")
	public ResponseEntity<?> clearCart(HttpSession session) {
		List<PlaceDTO> cart = (List<PlaceDTO>) session.getAttribute("cart");
		if (cart != null) {
			cart.clear(); // 장바구니 비우기
			session.setAttribute("cart", cart); // 세션 업데이트
			return ResponseEntity.ok().body("Cart cleared successfully.");
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found.");
	}

	@PostMapping("/removeItemFromCart")
	public ResponseEntity<String> removeItemFromCart(@RequestParam("p_id") int productId, HttpSession session) {
		// 세션에서 장바구니를 가져옴
		List<PlaceDTO> cart = (List<PlaceDTO>) session.getAttribute("cart");
		if (cart != null) {
			// 장바구니에서 해당 상품 제거
			cart.removeIf(item -> item.getP_id() == productId);
			session.setAttribute("cart", cart); // 변경된 장바구니를 세션에 다시 저장
		}
		return ResponseEntity.ok("상품이 장바구니에서 제거되었습니다.");
	}

	@GetMapping("mapPaths")
	public String getMapPaths(@RequestParam(name = "fromX") Double fromX, @RequestParam(name = "fromY") Double fromY,
			@RequestParam(name = "toX") Double toX, @RequestParam(name = "toY") Double toY,
			@RequestParam(name = "wayPoints", required = false) String wayPoints, Model model) {
		PlaceDTO fromPoint = new PlaceDTO(fromX, fromY);
		PlaceDTO toPoint = new PlaceDTO(toX, toY);
		List<PlaceDTO> wayPointList = new ArrayList<>();

		if (wayPoints != null && !wayPoints.isEmpty()) {
			String[] wayPointsArray = wayPoints.split("\\|");
			for (String point : wayPointsArray) {
				String[] coords = point.split(",");
				wayPointList.add(new PlaceDTO(Double.parseDouble(coords[0]), Double.parseDouble(coords[1])));
			}
		}

		try {
			List<PlaceDTO> placeList = KakaoApiUtil.getVehiclePaths(fromPoint, toPoint, wayPointList, "RECOMMEND");
			String placeListJson = new ObjectMapper().writeValueAsString(placeList);
			model.addAttribute("fromPoint", fromPoint);
			model.addAttribute("toPoint", toPoint);
			model.addAttribute("wayPoint2", wayPointList.isEmpty() ? null : wayPointList);
			model.addAttribute("placeList", placeListJson);

			System.out.println("kkkk" + placeListJson);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "경로를 계산하는 도중 오류가 발생했습니다.");
			return "error";
		}

		return "mapPaths";
	}

	@GetMapping("map")
	public String getMethodName() {
		return "map";
	}
}
