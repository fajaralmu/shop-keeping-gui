package com.fajar.shopkeeping.test;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResponseCategory implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1195345208045522504L;
	private String name, slug;
	@JsonAlias("term_id")
	private String termId;

}
