package com.finalpro.start.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.finalpro.start.dao.PlaceDAO;
import com.finalpro.start.dto.PlaceDTO;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PlaceService {

	@Autowired
	private PlaceDAO placeDAO;

	public String upLoadPlaceProc(List<MultipartFile> files, HttpSession session, PlaceDTO placeDTO,
			RedirectAttributes rttr) {

		log.info("upLoadPlaceProc()");

		String view = null;
		String msg = null;
		log.info("PlaceDTO {}", placeDTO);
		try {

//			placeDAO.upLoadPlaceProc(placeDTO);

			if (!files.get(0).isEmpty()) {
				fileUpLoad(files, session, placeDTO, placeDTO.getP_id());
				log.info("placeDTO {}", placeDTO);
			}
			placeDAO.upLoadPlaceProc(placeDTO);
			view = "redirect:/";
			msg = "장소 등록 성공";

		} catch (Exception e) {
			e.printStackTrace();
			view = "redirect:/upLoadPlace";
			msg = "장소 등록 실패";
		}
		log.info(msg);

		rttr.addFlashAttribute("msg", msg);

		return view;

	}

	private void fileUpLoad(List<MultipartFile> files, HttpSession session, PlaceDTO placeDTO, int p_id)
			throws IOException {
		log.info("fileUpLoad()");

		// 파일 저장 경로 설정
		String uploadDirectory = session.getServletContext().getRealPath("/"); // 원하는 고정된 디렉토리 경로로 변경하세요

		File folder = new File(uploadDirectory);
		if (!folder.exists()) {
			// 폴더가 존재하지 않으면 생성
			if (!folder.mkdirs()) {
				throw new IOException("Failed to create directory: " + uploadDirectory);
			}
		}

		for (MultipartFile multiPartFile : files) {
			// 파일명 추출
			String oriname = multiPartFile.getOriginalFilename();
			log.info("Original file name: {}", oriname);

			// 파일 저장 이름 생성
			String sysname = System.currentTimeMillis() + oriname.substring(oriname.lastIndexOf("."));
			log.info("System file name: {}", sysname);

			// 파일 저장 경로 설정
			File file = new File(uploadDirectory, sysname); // 디렉토리 경로와 파일 이름을 지정하여 생성

			// 파일 저장
			try {
				multiPartFile.transferTo(file);
			} catch (IOException e) {
				// 파일 저장 중에 예외 발생 시 처리
				log.error("Failed to upload file: {}", e.getMessage());
				throw e; // 예외 전파
			}

			// 파일 정보를 PlaceDTO에 설정 (필요한 경우)
			placeDTO.setP_iname(sysname);
			log.info(placeDTO.getP_iname());
			placeDTO.setP_id(p_id);
			log.info("PlaceDTO: {}", placeDTO);
		}
	}

	public List<PlaceDTO> getPlaceList(String p_location, String p_thema) {
		// 장소 리스트 가져오기
		return placeDAO.getPlaceList(p_location, p_thema);
	}

	// 장소 정보
	public PlaceDTO findById(int p_id) {

		return placeDAO.findById(p_id);
	}

	// 조회수 증
	public void increaseViews(int p_id) {
		placeDAO.increaseViews(p_id);
	}

	// 지역별 리스트
	public List<PlaceDTO> placeListByLocation(String p_location) {

		List<PlaceDTO> place = placeDAO.placeListByLocation(p_location);
		return place;
	}

	// 테마별 리스트
	public List<PlaceDTO> placeListByTheme(String p_thema) {
		List<PlaceDTO> place = placeDAO.placeListByTheme(p_thema);

		return place;
	}
}
