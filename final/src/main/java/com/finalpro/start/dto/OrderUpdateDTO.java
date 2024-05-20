package com.finalpro.start.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
// 드로그앤 드랍 값 담아놓는 dto
public class OrderUpdateDTO {
	
	
	private List<Integer> startPoint;
    private List<Integer> wayPoints;
    private List<Integer> endPoint;

	
}
