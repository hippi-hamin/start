package com.finalpro.start.util.kakaoUtil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {
	private Double x;
	private Double y;
	@JsonProperty("p_name")
	private String p_name;
	
	
	public Double getX() {
		return x;
	}
	
	public Double getY() {
		return y;
	}
	
	public String getP_name() {
		return p_name;
	}
}