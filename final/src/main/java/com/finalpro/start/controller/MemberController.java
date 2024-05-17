package com.finalpro.start.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.finalpro.start.dto.MemberDTO;
import com.finalpro.start.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MemberController {

	@Autowired
	private MemberService memberServ;

	@GetMapping("/")
	public String home() {
		log.info("home()");

		return "index";
	}

	@GetMapping("signup")
	public String signup() {
		log.info("signup()");
		return "signup";
	}

	@PostMapping("signupProc")
	public String signupProc(MemberDTO member, RedirectAttributes rttr) {
		log.info("signupProc()");
		// 생일 정보 LocalDate 형식으로 변환
		// 생일 정보를 LocalDate 형식으로 변환
		LocalDate birthday = member.getM_birthday();
		// 현재 날짜
		LocalDate currentDate = LocalDate.now();
		// 나이
		int age = memberServ.calculateAge(birthday, currentDate);
		// 계산된 나이 저장
		member.setM_age(age);
		log.info("member {}:", member);
		String view = memberServ.signupProc(member, rttr);
		return view;
	}

	@GetMapping("/signin")
	public String signin() {
		return "signin"; // 로그인 페이지로 이동
	}

	@PostMapping("/signinProc")
	public String signinProc(@RequestParam("m_email") String m_email, @RequestParam("m_pw") String m_pw,
			HttpSession session, RedirectAttributes rttr) {
		String view = memberServ.signinProc(m_email, m_pw, session, rttr);
		return view; // 로그인 처리 후 해당 뷰로 이동
	}

	@GetMapping("findEmail")
	public String findEmail() {
		return "findEmail";
	}

	@PostMapping("findEmailProc")
	public String findEmailProc(@RequestParam("m_name") String m_name, @RequestParam("m_phone") String m_phone,
			@RequestParam("m_gender") String m_gender, Model model) {
		log.info("findEmailProc()");
		String foundEmail = memberServ.findEmail(m_name, m_phone, m_gender);

		model.addAttribute("foundEmail", foundEmail);

		return "emailSearchResult"; // 이메일 찾기 결과 화면으로 이동
	}

	@GetMapping("resetPassword")
	public String findPassword() {
		return "resetPassword";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		HttpSession session = request.getSession(false); // 세션을 가져옴
		String msg = null;
		if (session != null) {
			session.invalidate(); // 세션 무효화
			msg = "로그아웃 되었습니다.";// 플래시 메시지 설정
		} else {
			msg = "이미 로그아웃 상태입니다.";
		}
		redirectAttributes.addFlashAttribute("msg", msg);
		return "redirect:/signin"; // 로그아웃 후 "/signin"으로 리다이렉트
	}

	@GetMapping("mypage")
	public String mypage(HttpSession session, Model model, RedirectAttributes rttr) {
		// 세션에서 로그인한 사용자 정보를 가져옴
		MemberDTO signedInUser = (MemberDTO) session.getAttribute("signedInUser");

		// 만약 로그인한 사용자가 없다면 로그인 페이지로 리다이렉트 또는 예외 처리
		if (signedInUser == null) {
			return "redirect:/";
		}
		// 가져온 사용자 정보를 모델에 추가
		log.info("signedInUser :{}", signedInUser);
		model.addAttribute("currentUser", signedInUser);
		model.addAttribute("msg", signedInUser.getM_name() + "님 환영합니다.");

		return "mypage";
	}

	// 비밀번호 변경
	@PostMapping("/changePassword")
	public String changePassword(@RequestParam("m_pw") String m_pw, HttpSession session, RedirectAttributes rttr) {
		log.info("changePassword()");
		String changePwEmail = (String) session.getAttribute("changePwEmail");
		log.info(changePwEmail);
		String view = memberServ.changePassword(m_pw, changePwEmail, rttr);
		return view;
	}

}
