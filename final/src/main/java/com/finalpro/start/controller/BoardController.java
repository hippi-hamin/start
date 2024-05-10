package com.finalpro.start.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.finalpro.start.dto.BoardDTO;
import com.finalpro.start.dto.MemberDTO;
import com.finalpro.start.service.BoardService;
import com.finalpro.start.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class BoardController {
	@Autowired
	private MemberService memberService;
	@Autowired
	private BoardService boardService;
	
	@GetMapping("/board")
	public String board() {
		return "board";
	}
	
	@GetMapping("/writeBoard")
	public String writeBoard(HttpSession session, Model model, RedirectAttributes rttr) {
		// 세션에서 로그인 여부 확인
		MemberDTO signedInUser = (MemberDTO) session.getAttribute("signedInUser");
		
		// 만약 로그인된 경우 게시글 작성 페이지로 이동
		if (signedInUser != null) {
			return "writeBoard";
			
		// 로그인 되어있지 않은 경우, singin으로 이동
		} else {
			model.addAttribute("msg", "로그인 시에만 이용가능합니다.");
			return "redirect:/signin";
		}
	}
	
	@PostMapping("/writeBoard")
	public String writeBoard(@ModelAttribute BoardDTO boardDTO, HttpSession session) {
		System.out.println("boardDTO = " + boardDTO);
		boardService.writeBoard(boardDTO, session);
		return "index2";
	}
	
}
