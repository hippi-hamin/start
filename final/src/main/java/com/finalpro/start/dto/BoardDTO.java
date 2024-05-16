package com.finalpro.start.dto;

import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("board")
public class BoardDTO {
	private int b_id;
	private int m_id;
	private String b_title;
	private String b_content;
	private int b_views;
	private LocalDateTime b_createdAt;
	private LocalDateTime b_updatedAt;
	// 기본 생성자
	public BoardDTO() {
	}
}
