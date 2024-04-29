package com.finalpro.start.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.finalpro.start.service.MailService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MailController {
	@Autowired
	private MailService mailService;

	@PostMapping("sendEmail")
	public String sendEmail(@RequestParam("m_email") String m_email, HttpSession session) {
		log.info("sendEmail()");
		String result = mailService.sendEmail(m_email, session);

		// 전송 결과에 따라 적절한 처리 수행
		if (result.equals("ok")) {
			// 성공
			session.setAttribute("changePwEmail", m_email);
			return "emailVerificationPage";
		} else {
			// 실패
			return "redirect:resetPassword";
		}
	}

	@PostMapping("verifyCode")
	public String verifyCode(@RequestParam("v_code") String v_code, HttpSession session) {
		log.info("verifyCode()");
		// 세션에 저장된 인증코드 가져옴
		String authNum = (String) session.getAttribute("authNum");
		log.info(authNum);
		// 입력한 코드와 저장된 코드 비교
		if (v_code.equals(authNum)) {
			// 일치
			return "changePassword";
		} else {
			return "redirect:resetPassword";
		}
	}
}
