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

	@GetMapping("upLoadPlace")
	public String upLoadPlace() {
		log.info("upLoad()");
		return "upLoadPlace";
	}

	// 장소 등록
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

	// 장소 리스트 페이지 이동(테마별)
	@GetMapping("placeList")
	public String placeList(Model model, @RequestParam(value="p_location") String p_location,
			@RequestParam(value="p_thema") String p_thema) {
    log.info("placeList()");
		List<PlaceDTO> placeList = placeService.getPlaceList(p_location, p_thema);
		model.addAttribute("placeList", placeList);
		return "placeList";
	}

	@GetMapping("placeListByLocation")
	public String placeListByLocation(@RequestParam(value="p_location") String p_location, Model model) {
		String view = null;
		List<PlaceDTO> place = placeService.placeListByLocation(p_location);
		if (place != null) {
			model.addAttribute("placeListByLocation", place);
			view = "placeListByLocation";
		} else {
			view = "placeListByLocation";
		}

		return view;
	}

	// 테마별 리스트
	@GetMapping("placeListByTheme")
	public String placeByTheme(@RequestParam(value="p_thema") String p_thema, Model model) {
		String view = null;
		List<PlaceDTO> place = placeService.placeListByTheme(p_thema);
		if (place != null) {
			model.addAttribute("placeListByTheme", place);
			view = "placeListByTheme";
		} else {
			view = "placeListByTheme";
		}
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
			String uploadDirectory = session.getServletContext().getRealPath("/"); // 업로드된 이미지 파일이 있는 경로
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

	@GetMapping("searchPlace")
	public String getPlacePath(@RequestParam(name = "x", required = false) Double x,
			@RequestParam(name = "y", required = false) Double y,
			@RequestParam(name = "keyword", required = false) String keyword, Model model)
			throws IOException, InterruptedException {
		if (x != null && y != null && keyword != null) {
			List<PlaceDTO> keywordPlaceList = KakaoApiUtil.getPlaceByKeyWord(keyword, new PlaceDTO(x, y));
			String keywordPlaceListJson = new ObjectMapper().writer().writeValueAsString(keywordPlaceList);
			model.addAttribute("keywordPlacetList", keywordPlaceListJson);

			List<PlaceDTO> pathPlaceList = new ArrayList<>();
			for (int i = 1; i < keywordPlaceList.size(); i++) {
				PlaceDTO prevPoint = keywordPlaceList.get(i - 1);
				PlaceDTO nextPoint = keywordPlaceList.get(i);
				pathPlaceList.addAll(KakaoApiUtil.getVehiclePaths(prevPoint, nextPoint, null));
			}
			String pathPlaceListJson = new ObjectMapper().writer().writeValueAsString(pathPlaceList);
			model.addAttribute("pathPlaceList", pathPlaceListJson);
		}
		return "searchPlace";
	}

	@GetMapping("map")
	public String getMethodName() {
		return "map";
	}

}
