package com.fajar.shopkeeping.model;

import java.io.Serializable;

import org.springframework.http.ResponseEntity;

import com.fajar.shopkeeping.constant.ReportType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 8254712638473452332L;
	private ReportType reportType;
	private ResponseEntity<byte[]> fileResponse;

}
