package com.fajar.shopkeeping.webservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.User;

import static com.fajar.shopkeeping.constant.WebServiceConstants.*;

import javax.swing.JOptionPane;

public class AccountService {

	private RestTemplate restTemplate = RestComponent.getRestTemplate();

	private static AccountService app;

	public static AccountService getInstance() {
		if (null == app) {
			app = new AccountService();
		}
		return app;
	}

	private AccountService() {

	}

	public String getAppId() {
		ShopApiResponse appIdResponse = requestAppId();
		if (appIdResponse == null) {
			return null;
		}

		System.out.println("app id response: " + appIdResponse);

		return appIdResponse.getMessage();
	}

	private ShopApiResponse requestAppId() {
		try {
			ResponseEntity<ShopApiResponse> response = restTemplate.postForEntity(URL_REQIEST_APP, new ShopApiRequest(),
					ShopApiResponse.class);
			return response.getBody();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	public void doLogin(String username, String password) {
		// TODO Auto-generated method stub
		ShopApiRequest loginRequest = new ShopApiRequest();
		User user = User.builder().username(username).password(password).build();
		loginRequest.setUser(user);

		try {
			ResponseEntity<ShopApiResponse> response = restTemplate.postForEntity(URL_LOGIN,
					RestComponent.addAuthRequest(loginRequest), ShopApiResponse.class);

			JOptionPane.showMessageDialog(null, "Login Success!");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Login Error: " + e.getMessage());
		}

	}

}
