package com.finalpro.start.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    @Autowired
    private PlatformService platformService;

    public String upLoadPlaceProc(List<MultipartFile> files, HttpSession session, PlaceDTO placeDTO,
            RedirectAttributes rttr) {
        String view = null;
        String msg = null;

        try {
            // 파일 업로드 처리
            List<String> uploadedFileNames = uploadFiles(files, session);
            placeDTO.setP_iname(String.join(", ", uploadedFileNames)); // 파일명을 PlaceDTO에 설정

            // 데이터베이스에 저장
            savePlaceDetails(placeDTO);

            view = "redirect:/";
            msg = "장소 등록 성공";
        } catch (Exception e) {
            log.error("Error processing place upload: ", e);
            view = "redirect:/upLoadPlace";
            msg = "장소 등록 실패";
            rttr.addFlashAttribute("msg", msg);
        }

        rttr.addFlashAttribute("msg", msg);
        return view;
    }

    private List<String> uploadFiles(List<MultipartFile> files, HttpSession session) throws IOException {
        List<String> uploadedFileNames = new ArrayList<>();
        String uploadDirectory = session.getServletContext().getRealPath("/uploads/");
        File folder = new File(uploadDirectory);

        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Failed to create directory: " + uploadDirectory);
        }

        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            String storedFileName = System.currentTimeMillis() + "_" + originalFilename;
            File targetFile = new File(folder, storedFileName);
            file.transferTo(targetFile);
            uploadedFileNames.add(storedFileName);
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

    public void upLoadPlaceProc(PlaceDTO placeDTO) {
        placeDAO.upLoadPlaceProc(placeDTO);
    }
}
