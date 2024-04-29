package com.finalpro.start.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.finalpro.start.dao.MemberDAO;
import com.finalpro.start.dto.MemberDTO;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MemberService {

	@Autowired
	private MemberDAO memberDAO;

	@Autowired(required = false)
	private BCryptPasswordEncoder passwordEncoder;

	// 회원가입
	public String signupProc(MemberDTO member, RedirectAttributes rttr) {
		log.info("signupProc()");
		String view = null;
		String msg = null;
		log.info("MemberDTO {}", member);
		// 비밀번호 암호화
		String encodePassword = passwordEncoder.encode(member.getM_pw());
		member.setM_pw(encodePassword);

		try {
			// 회원가입 처리
			memberDAO.signupProc(member);

			// 회원가입 성공 시 포인트 증가
			int currentPoint = member.getM_point();
			currentPoint += 1000;
			member.setM_point(currentPoint);

			// 포인트 증가 후 DB에 반영
			memberDAO.updateM_point(member);

			// 회원가입 후 메인 페이지로 리다이렉트
			view = "redirect:/";
			msg = "가입 성공. 1000point 적립되었습니다.";
			log.info("currentPoint {}", member.getM_point());
		} catch (Exception e) {
			e.printStackTrace();
			view = "redirect:signup";
			msg = "실패하였습니다. 다시 시도해주세요.";
		}
		rttr.addFlashAttribute("msg", msg);

		return view;
	}

	// 로그인
	public String signinProc(String m_email, String m_pw, HttpSession session, RedirectAttributes rttr) {
		log.info("signinProc()");
		String view = null;
		String msg = null;

		// 이메일로 사용자 정보 가져오기
		MemberDTO userInfo = memberDAO.findByEmail(m_email);
		log.info("userInfo {}", userInfo);
		if (userInfo != null) {
			// 입력한 비밀번호와 디비에 저장된 비밀번호 비교
			if (passwordEncoder.matches(m_pw, userInfo.getM_pw())) {
				// 로그인 성공
				session.setAttribute("signedInUser", userInfo);

				view = "redirect:/"; // 로그인 성공 후 이동할 페이지 (홈으로 리다이렉트)
			} else {
				// 비밀번호 일치하지 않을 시
				msg = "다시 시도해주세요.";
				rttr.addFlashAttribute("msg", msg); // 메세지 전달
				view = "redirect:signin"; // 로그인 페이지로 리다이렉트
			}
		} else {
			// 사용자 존재하지 않을 시
			msg = "다시 시도해주세요.";
			rttr.addFlashAttribute("msg", msg); // 메세지 전달
			view = "redirect:signin"; // 로그인 페이지로 리다이렉트
		}

		return view;
	}

	// 이메일 중복
	public MemberDTO checkEmail(String m_email) {
		log.info("checkEmail()", m_email);
		MemberDTO member = memberDAO.checkEmail(m_email);
		return member;
	}

	// 이메일 찾기
	public String findEmail(String m_name, String m_phone, String m_gender) {
		log.info("findEmail service");
		// DAO를 사용하여 데이터베이스에서 이메일을 조회하고 결과를 반환합니다.
		String result = memberDAO.findEmail(m_name, m_phone, m_gender);
		log.info("result {}", result);
		return result;
	}

	// 생일로 입력 받아 나이로 환산하기
	public int calculateAge(LocalDate birthday, LocalDate currentDate) {
		int age = currentDate.getYear() - birthday.getYear();

		// 생일이 지났는지 확인
		if (birthday.getMonthValue() > currentDate.getMonthValue()
				|| birthday.getMonthValue() == currentDate.getMonthValue()
						&& birthday.getDayOfMonth() > currentDate.getDayOfMonth()) {
			age--;

		}
		return age;
	}

	// 비밀번호 변경
	public String changePassword(String m_pw, String changePwEmail, RedirectAttributes rttr) {
		String view = null;
		String msg = null;
		String encodePassword = passwordEncoder.encode(m_pw);
		if (memberDAO.changePassword(encodePassword, changePwEmail)) {
			msg = "비밀번호가 변경되었습니다.";
			view = "redirect:signin";
		} else {
			msg = "다시 시도해주세요.";
			view = "redirect:changePassword";
		}
		rttr.addFlashAttribute("msg",msg);
		return view;
	}
}
