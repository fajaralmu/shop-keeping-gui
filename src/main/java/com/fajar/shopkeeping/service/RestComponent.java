package com.fajar.shopkeeping.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fajar.dto.ShopApiRequest;
import com.fajar.shopkeeping.handler.AppHandler;

public class RestComponent {
	
	private static final RestTemplate restTemplate = new RestTemplate();
	
	public static RestTemplate getRestTemplate() { 
//		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();        
//		//Add the Jackson Message converter
//		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//
//		// Note: here we are making this converter to process any kind of response, 
//		// not only application/*json, which is the default behaviour
//		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));        
//		messageConverters.add(converter);
//		restTemplate.setMessageConverters(messageConverters); 
		return restTemplate;
	}
	
	public static HttpHeaders buildAuthHeader(boolean withLoginKey ){
 
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("requestId", AppSession.getApplicationID());
		headers.set("content-type", "application/json");
		headers.set("loginKey", AppSession.getLoginKey());
 
		return headers ;
	}
	
	public static HttpEntity<ShopApiRequest> buildEmptyAuthRequest(boolean withLoginKey){
		return new HttpEntity<ShopApiRequest>(new ShopApiRequest(), buildAuthHeader(withLoginKey));
	}
	
	public static HttpEntity<ShopApiRequest> buildAuthRequest(ShopApiRequest shopApiRequest, boolean withLoginKey) {
		return new HttpEntity<ShopApiRequest>(shopApiRequest, buildAuthHeader(withLoginKey));
	}

}