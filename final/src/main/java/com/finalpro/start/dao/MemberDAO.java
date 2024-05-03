package com.finalpro.start.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.finalpro.start.dto.MemberDTO;

@Repository
@Mapper
public interface MemberDAO {
	
	// 회워가입
    void signupProc(MemberDTO member);
    // 회원가입 성공 시 포인트 증가 
	void updateM_point(MemberDTO member);
	// 입력한 이메일과 일치하는 회원 정보 찾기  
	MemberDTO findByEmail(String m_email);
	// 이메일 중복 
	MemberDTO checkEmail(String m_email);
	// 이메일 찾기
	String findEmail(@Param("m_name") String m_name, @Param("m_phone") String m_phone, 
						@Param("m_gender") String m_gender);
	// 
	String selectEmail(int m_id);
	// 비밀번호 변경 
	boolean changePassword(@Param("encodePassword") String encodePassword, @Param("changePwEmail") String changePwEmail);
	// 회원 정보 변경 
	boolean updateUserInfo(String m_name, String m_phone, String m_email);
	
}
