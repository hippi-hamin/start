package com.finalpro.start.util.kakaoUtil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {
	private Double x;
	private Double y;

	public Double getX() {
		return x;
	}
	
	public Double getY() {
		return y;
	}
}