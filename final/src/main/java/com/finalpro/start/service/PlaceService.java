package com.finalpro.start.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 필요없는 라이브러리 삭제 -안재문- 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
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
	// 필요없는 클래스 삭제 -안재문-

	public String upLoadPlaceProc(List<MultipartFile> files, HttpSession session, PlaceDTO placeDTO,

			RedirectAttributes rttr) {
		String view = null;
		String msg = null;

		try {
			// 파일 업로드 처리
			List<String> uploadedFileNames = fileUpLoad(files, session);

			placeDTO.setP_iname(String.join(", ", uploadedFileNames)); // 파일명을 PlaceDTO에 설정

			// 데이터베이스에 저장
			savePlaceDetails(placeDTO);

			// 로그 출력
			log.info("placeDTO {}", placeDTO);

			// DAO를 통해 장소 등록 처리
			// placeDAO.upLoadPlaceProc(placeDTO); // 이미 저장된 파일을 다시 업로드할 필요가 없습니다.

			view = "redirect:/";
			msg = "장소 등록 성공";
		} catch (Exception e) {
			log.error("Error processing place upload: ", e);
			view = "redirect:/upLoadPlace";
			msg = "장소 등록 실패";
			rttr.addFlashAttribute("errorMessage", msg);
		}

		rttr.addFlashAttribute("msg", msg);
		return view;
	}

	private List<String> fileUpLoad(List<MultipartFile> files, HttpSession session) throws IOException {
		List<String> uploadedFileNames = new ArrayList<>();
		String uploadDirectory = "C:\\Development\\upLoad\\";

		File folder = new File(uploadDirectory);

		if (!folder.exists() && !folder.mkdirs()) {
			throw new IOException("Failed to create directory: " + uploadDirectory);
		}

		for (MultipartFile file : files) {
			if (!file.isEmpty()) { // 파일이 비어있지 않을 때만 업로드 수행
				String originalFilename = file.getOriginalFilename();
				String storedFileName = System.currentTimeMillis() + "_" + originalFilename;
				log.info(storedFileName);
				File targetFile = new File(folder, storedFileName);
				file.transferTo(targetFile);
				uploadedFileNames.add(storedFileName);
			}
		}

		return uploadedFileNames;
	}

	private void savePlaceDetails(PlaceDTO placeDTO) {
		placeDAO.upLoadPlaceProc(placeDTO);
	}

	public List<PlaceDTO> getPlaceList(String p_location, String p_thema) {
		// 장소 리스트 가져오기
		return placeDAO.getPlaceList(p_location, p_thema);
	}

	// 파라미터x시 리스트
	public List<PlaceDTO> getPlaceList(Model model) {
		List<PlaceDTO> result = placeDAO.getPlaceList(null, null);
		model.addAttribute("placeList", result);
		return result;

	}

	public PlaceDTO findById(int p_id) {
		return placeDAO.findById(p_id);
	}

	public void increaseViews(int p_id) {
		placeDAO.increaseViews(p_id);
	}

	public List<PlaceDTO> placeListByLocation(String p_location) {
		return placeDAO.placeListByLocation(p_location);
	}

	public List<PlaceDTO> placeListByTheme(String p_thema) {
		return placeDAO.placeListByTheme(p_thema);
	}

	public void savePlace(PlaceDTO place) {
		placeDAO.savePlace(place);
	}

	// 장소 수정 오류 해결 -안재문
	public String updatePlaceProc(List<MultipartFile> files, HttpSession session, PlaceDTO placeDTO,
			RedirectAttributes rttr) {
		log.info("updatePlaceProc(), service");
		String view = null;
		String msg = null;
		log.info("PlaceDTO {}", placeDTO);
		try {
			if (!files.isEmpty()) {
// 기존 이미지 삭제
//				deleteOldImage(placeDTO, session);

// 새로운 이미지 업로드 및 파일 이름 설정
				List<String> uploadedFileNames = fileUpLoad(files, session);
				if (!uploadedFileNames.isEmpty()) {
					placeDTO.setP_iname(uploadedFileNames.get(0)); // 첫 번째 파일 이름 설정
				}
				log.info("Updated placeDTO with new image name: {}", placeDTO);
			}

// 파일 업로드 이후에 장소 정보 업데이트
			placeDAO.updatePlaceProc(placeDTO);

			view = "redirect:/";
			msg = "장소 수정 성공";
		} catch (Exception e) {
			e.printStackTrace();
			view = "redirect:/updatePlace";
			msg = "장소 수정 실패";
		}
		log.info(msg);
		rttr.addFlashAttribute("msg", msg);
		return view;
	}

	// 필요없는 메서드 삭제 -안재문-
	// make plan 필터
	public List<PlaceDTO> searchByFilters(List<String> themes, List<String> regions) {
		log.info("themes : " + themes);
		log.info("regions : " + regions);
		List<PlaceDTO> result = placeDAO.searchByFilters(themes, regions);
		log.info("result : " + result);
		return result;
	}

	// 지역별 리스트
	public List<PlaceDTO> fetchPlacesLocation(String location) {
		log.info(location);

		List<PlaceDTO> result = placeDAO.fetchPlacesLocation(location);

		return result;
	}

	// 테마별 리스트
	public List<PlaceDTO> fetchPlacesTheme(String theme) {
		log.info(theme);
		List<PlaceDTO> result = placeDAO.fetchPlacesTheme(theme);
		return result;
	}

	// 장소 삭제 메소드
	public String deletePlace(int p_id, RedirectAttributes rttr) {
		
		String view = null;
		String msg = null;
		
		try {
			log.info("deletePlace 실행 성공");
			placeDAO.deletePlace(p_id);
			
			msg = "삭제 성공!";
			view = "redirect:/adminPage";
			
		} catch (Exception e) {
			log.info("deletePlace 실행 오류");
			
			msg = "삭제 실패!";
			view = "redirect:/";
		}
		
		rttr.addFlashAttribute("msg", msg);
		return view;
	}

}
