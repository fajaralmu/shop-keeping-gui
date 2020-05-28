package com.fajar.shopkeeping.service;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import com.fajar.shoppingmart.dto.WebRequest;

public class RestComponent {
	
	private static final RestTemplate restTemplate = new RestTemplate();
	private static final String HEADER_ATTR_REQ_ID = "requestId";
	private static final String HEADER_ATTR_CONTENT_TYPE = "content-type";
	private static final String HEADER_ATTR_LOGIN_KEY = "loginKey";
	
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
		headers.set(HEADER_ATTR_REQ_ID, AppSession.getApplicationID());
		headers.set(HEADER_ATTR_CONTENT_TYPE, "application/json");
		headers.set(HEADER_ATTR_LOGIN_KEY, AppSession.getLoginKey());
 
		return headers ;
	}
	
	public static HttpEntity<WebRequest> buildEmptyAuthRequest(boolean withLoginKey){
		return new HttpEntity<WebRequest>(new WebRequest(), buildAuthHeader(withLoginKey));
	}
	
	public static HttpEntity<WebRequest> buildAuthRequest(WebRequest WebRequest, boolean withLoginKey) {
		return new HttpEntity<WebRequest>(WebRequest, buildAuthHeader(withLoginKey));
	}
	
	public static HttpEntity<Map> buildAuthRequest(Map WebRequest, boolean withLoginKey) {
		return new HttpEntity<Map>(WebRequest, buildAuthHeader(withLoginKey));
	}

}
