package com.finalpro.start.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finalpro.start.dao.BoardDAO;
import com.finalpro.start.dto.BoardDTO;
import com.finalpro.start.dto.MemberDTO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BoardService {
	@Autowired
	private BoardDAO boardDAO;
	
	public void writeBoard(BoardDTO boardDTO, HttpSession session) {
		MemberDTO userInfo = (MemberDTO) session.getAttribute("signedInUser");
		int m_id = userInfo.getM_id();
		boardDTO.setM_id(m_id);
		
		boardDAO.writeBoard(boardDTO);
	}
}
