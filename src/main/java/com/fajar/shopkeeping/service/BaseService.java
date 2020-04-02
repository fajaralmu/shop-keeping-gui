package com.fajar.shopkeeping.service;

import org.springframework.web.client.RestTemplate;

public class BaseService {


	protected static final String HEADER_LOGIN_KEY = "loginKey";

	protected RestTemplate restTemplate = RestComponent.getRestTemplate();
}
