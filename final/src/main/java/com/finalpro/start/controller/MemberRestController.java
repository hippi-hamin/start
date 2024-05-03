package com.finalpro.start.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finalpro.start.dto.MemberDTO;
import com.finalpro.start.service.MemberService;

import jakarta.servlet.http.HttpSession;
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
	
	 @PostMapping("/updateUserInfo")
	    public ResponseEntity<String> updateUserInfo(@RequestParam Map<String, String> userInfo, HttpSession session) {
	        // 세션에서 로그인한 사용자 정보를 가져옴
	        MemberDTO signedInUser = (MemberDTO) session.getAttribute("signedInUser");
	        
	        if (signedInUser == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인되지 않은 사용자입니다.");
	        }

	        String m_name = userInfo.get("m_name");
	        String m_phone = userInfo.get("m_phone");

	        // 사용자 정보 업데이트
	        String result = memberServ .updateUserInfo(m_name, m_phone, signedInUser.getM_email());

	        if (result.equals("success")) {
	            return ResponseEntity.ok("사용자 정보가 성공적으로 업데이트되었습니다.");
	        } else {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 정보 업데이트에 실패했습니다.");
	        }
	    }
}
