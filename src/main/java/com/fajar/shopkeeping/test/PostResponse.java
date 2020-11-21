package com.fajar.shopkeeping.test;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PostResponse implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -2328541386817117984L;
	private ResponseCategory category;
	private int total;
	@JsonAlias("current_page")
	private int currentPage;
	@JsonAlias("per_page")
	private int perPage;
	
	private Object posts;
	
	@JsonIgnore
	private List<Post > agendas;
	@JsonIgnore
	private NewsPost newsPost;
}
