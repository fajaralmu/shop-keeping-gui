package com.fajar.shopkeeping.test;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NewsPost implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4513128081788042103L;
	private Post begin;
	private List<Post> remains;

}
