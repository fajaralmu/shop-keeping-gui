package com.fajar.shopkeeping.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SharedContext implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 4327130351892102567L;
	private int month;
	private int year;
	private int day;
	
	private int minTransactionYear;
	private int code;
	
	public SharedContext(int d, int m, int y) {
		day = d;
		month = m;
		year = y;
	}

}
