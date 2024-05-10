package com.finalpro.start.dao;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.finalpro.start.dto.BoardDTO;

import lombok.RequiredArgsConstructor;

@Repository
@Mapper
public interface BoardDAO {
	
	// 게시물 등록
	void writeBoard(BoardDTO boardDTO);	

	// 게시물 수정

	// 게시물 삭제
	
	// 댓글 등록
	
	// 댓글 삭제
	
	//댓글 수정

	// 조회수
	
	// 게시물 추천

	// 답글 등록

}
