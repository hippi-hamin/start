package com.finalpro.start.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finalpro.start.dto.MemberDTO;
import com.finalpro.start.service.MemberService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class MemberRestController {

	@Autowired
	private MemberService memberServ;

	@PostMapping("/checkEmail")
	public String checkEmail(@RequestParam("m_email") String m_email) {
		log.info("checkEmail()");
		log.info("m_email: {}", m_email);

		// 이메일을 통해 회원 정보를 조회
		MemberDTO member = memberServ.checkEmail(m_email);
		log.info("memberDTO: {}", member);

		// member가 null이거나 member의 이메일이 입력받은 이메일과 일치하지 않는 경우
		if (member == null || !member.getM_email().equals(m_email)) {
			// 존재하지 않음 - 사용 가능
			return "available";
		} else {
			// 존재 - 사용 불가
			return "exists";
		}
	}
}
