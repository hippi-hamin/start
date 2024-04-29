package com.finalpro.start.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import com.finalpro.start.dto.PlaceDTO;
import com.finalpro.start.service.PlaceService;

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
	public String placeList(Model model) {
		log.info("placeList()");
		List<PlaceDTO> placeList = placeService.getPlaceList();
		model.addAttribute("placeList", placeList);
		return "placeList";
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
	

	@GetMapping("apitest")
	public String apitest() {
		return "apitest";
	}
}
