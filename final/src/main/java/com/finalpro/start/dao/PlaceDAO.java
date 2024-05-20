package com.finalpro.start.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.finalpro.start.dto.PlaceDTO;

@Repository
@Mapper
public interface PlaceDAO {
	// 장소 등록
	void upLoadPlaceProc(PlaceDTO placeDTO);

	// 장소 리스트
	List<PlaceDTO> getPlaceList(@Param("p_location") String p_location, @Param("p_thema") String p_thema);

	// 장소정보 가져오기
	PlaceDTO findById(int p_id);

	// 조회수 증가
	void increaseViews(int p_id);

	// 지역별 리스트
	List<PlaceDTO> placeListByLocation(@Param("p_location") String p_location);

	// 테마별 리스트
	List<PlaceDTO> placeListByTheme(@Param("p_thema") String p_thema);

	// 선택한 장소 저장
	void savePlace(PlaceDTO place);

	// 장소 수정
	void updatePlaceProc(PlaceDTO placeDTO);

	// 지역별 검색
	List<PlaceDTO> searchByRegion(@Param("regions") List<String> regions);

	// 테마별 검색
	List<PlaceDTO> searchByTheme(@Param("themes") List<String> themes);

	List<PlaceDTO> searchByFilters(@Param("themes") List<String> themes, @Param("regions") List<String> regions);
}
