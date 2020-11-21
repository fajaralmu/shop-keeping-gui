package com.fajar.shopkeeping.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fajar.shoppingmart.util.MapUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestEntityProp {

	public static void main(String[] args) throws Exception {
		PostResponse response = callGetNews(2);
		System.out.println(response.getPosts().getClass());
		List<Post> posts = new ArrayList<Post>();
		if (response.getPosts() instanceof List) {
			List rawPosts = (List) response.getPosts();

			posts = (MapUtil.convertMapList(rawPosts, Post.class));
			for (Post post : posts) {
				System.out.println("post: " + post);
			}

		} else if (response.getPosts() instanceof Map) {
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(response.getPosts());
			NewsPost newsPost = mapper.readValue(json, NewsPost.class);
			posts = newsPost.getRemains();
			for (int i = 0; i < posts.size(); i++) {
				Post p = posts.get(i);
				System.out.println(i+" "+p);
			}
			//System.out.println("newsPost.getRemains(): " + posts.get(0));
		}

	}

	public static PostResponse getAgenda() {
		PostResponse response = callGetAgenda();
		
		if (response.getPosts() instanceof List) {
			List rawPosts = (List) response.getPosts();
			List<Post> posts = new ArrayList<Post>();
			posts = (MapUtil.convertMapList(rawPosts, Post.class));
			response.setAgendas(posts);

		}
		return response;
	}

	public static PostResponse getNews(int page) {
		PostResponse response = callGetNews(page);
		if (response.getPosts() instanceof Map) {
			NewsPost newsPost = MapUtil.mapToObject((Map) response.getPosts(), NewsPost.class);
			response.setNewsPost(newsPost);
		}
		return response;
	}

	static final RestTemplate REST_TEMPLATE = new RestTemplate();

	static HttpEntity<String> httpEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		return entity;

	}

	private static PostResponse callGetAgenda() {
		String endPoint = "http://kafila.sch.id/index.php/api/homepage/agenda";

		ResponseEntity<PostResponse> response = REST_TEMPLATE.exchange(endPoint, HttpMethod.GET, httpEntity(),
				PostResponse.class);
		return response.getBody();
	}

	public static PostResponse callGetNews(int page) {
		String endPoint = "http://kafila.sch.id/index.php/api/homepage/news?page=" + page;
		ResponseEntity<PostResponse> response = REST_TEMPLATE.exchange(endPoint, HttpMethod.GET, httpEntity(),
				PostResponse.class);
		return response.getBody();
	}

}
