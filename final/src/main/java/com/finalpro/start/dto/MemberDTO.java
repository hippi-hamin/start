package com.finalpro.start.dto;

import java.time.LocalDate;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("member")
public class MemberDTO {

	private int m_id;
	private String m_email;
	private String m_pw;
	private String m_name;
	private String m_gender;
	private LocalDate m_birthday;
	private int m_age;
	private int m_point;
	private String m_phone;

}
