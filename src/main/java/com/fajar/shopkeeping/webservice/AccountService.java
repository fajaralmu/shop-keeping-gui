package com.fajar.shopkeeping.webservice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.User;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.component.Loadings;
import com.fajar.shopkeeping.handler.AppHandler;

import static com.fajar.shopkeeping.constant.WebServiceConstants.*;

import java.util.List;

import javax.activity.InvalidActivityException;
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
			e.printStackTrace();
			return null;
		}
	}

	public void doLogin(final String username, final String password) {

		Loadings.start();

		Thread thread = new Thread(new Runnable() {

			public void run() {

				ShopApiRequest loginRequest = new ShopApiRequest();
				User user = User.builder().username(username).password(password).build();
				loginRequest.setUser(user);

				try {
					ResponseEntity<ShopApiResponse> response = restTemplate.postForEntity(URL_LOGIN,
							RestComponent.addAuthRequest(loginRequest), ShopApiResponse.class);

					if (response.getBody().getCode().equals("00") == false) {
						throw new InvalidActivityException("Invalid Response");
					}

					HttpHeaders responseHeaders = response.getHeaders();
					List<String> loginKey = responseHeaders.get("loginKey");
					AppHandler.setLoginKey(loginKey.get(0));

					Loadings.end();
					Dialogs.showInfoDialog("Login Success!");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					Dialogs.showErrorDialog("Login Error: " + e.getMessage());
				} finally {
					Loadings.end();
				}
			}

		});
		thread.start();

	}

}
