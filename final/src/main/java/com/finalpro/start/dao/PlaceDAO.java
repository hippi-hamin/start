package com.finalpro.start.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.finalpro.start.dto.PlaceDTO;

@Repository
@Mapper
public interface PlaceDAO {
	// 장소 등로
	void upLoadPlaceProc(PlaceDTO placeDTO);

	// 장소 리스트
	List<PlaceDTO> getPlaceList(@Param("p_location")String p_location, @Param("p_thema")String p_thema, @Param("p_people")String p_people);

	// 장소정보 가져오기
	PlaceDTO findById(int p_id);

	// 조회수 증가
	void increaseViews(int p_id);
	
}
