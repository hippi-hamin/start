package com.finalpro.start.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpsRedirectSpec;
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
    @Autowired
    private PlatformService platformService;

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
        String uploadDirectory = "/Users/upLoad/";

        File folder = new File(uploadDirectory);

        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Failed to create directory: " + uploadDirectory);
        }

        for (MultipartFile file : files) {
            if (!file.isEmpty()) { // 파일이 비어있지 않을 때만 업로드 수행
                String originalFilename = file.getOriginalFilename();
                String storedFileName = System.currentTimeMillis() + "_" + originalFilename;
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
    public List<PlaceDTO> getPlaceList() {
        return placeDAO.getPlaceList(null, null);
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


    public String updatePlaceProc(List<MultipartFile> files, HttpSession session, PlaceDTO placeDTO,
			RedirectAttributes rttr) {
		log.info("updatePlaceProc(), service");
		String view = null;
		String msg = null;
		log.info("PlaceDTO {}", placeDTO);
		try {
			if (!files.isEmpty()) {
				// 기존 이미지 삭제
				deleteOldImage(placeDTO, session);
				// 새로운 이미지 업로드
				fileUpLoad(files, session);
				log.info("placeDTO {}", placeDTO);
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

	private void deleteOldImage(PlaceDTO placeDTO, HttpSession session) throws Exception {
		log.info("deleteOldImage()");
		log.info("placeDTO {}:", placeDTO);
		String uploadDirectory = "/Users/upLoad/"; // 이미지 업로드 디렉토리
		String imagePath = uploadDirectory + placeDTO.getP_iname(); // 이미지 경로
		log.info(imagePath);
		File file = new File(imagePath);
		if (file.exists()) {
			if (file.delete()) {
				log.info("기존 이미지 삭제 성공: {}", imagePath);
			} else {
				log.error("기존 이미지 삭제 실패: {}", imagePath);
				throw new Exception("기존 이미지 삭제 실패");
			}
		} else {
			log.warn("삭제할 이미지가 존재하지 않습니다: {}", imagePath);
		}
	}
	// 지역별 검색 
	public List<PlaceDTO> searchByRegion(List<String> regions) {
		
		return placeDAO.searchByRegion(regions);
	}

}
