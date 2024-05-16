package com.finalpro.start.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("place")
public class PlaceDTO {
	
	private int p_id;
	private String p_name;
	private String p_location;
	private String p_description;
	private String p_thema;
	private String p_iname;
	private int p_views;
	private int p_stScore;
	private int p_price;
	private Double x;
	private Double y;
	// 기본 생성자
    public PlaceDTO() {
    }
    // 두 개의 인수를 받는 생성자
    public PlaceDTO(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
