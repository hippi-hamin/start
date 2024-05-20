package com.finalpro.start.controller;

import java.nio.file.Path;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OrderUpdateDTO {
	
	private List<Integer> startPoint;
    private List<Integer> wayPoints;
    private List<Integer> endPoint;

	
}
