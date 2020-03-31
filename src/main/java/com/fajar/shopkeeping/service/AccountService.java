package com.fajar.shopkeeping.service;

import static com.fajar.shopkeeping.constant.WebServiceConstants.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activity.InvalidActivityException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.User;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.component.Loadings;
import com.fajar.shopkeeping.util.MapUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AccountService {

	protected static final String HEADER_LOGIN_KEY = "loginKey";

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

	public void getAppId(MyCallback callback) {

		requestAppId(callback);

	}

	/**
	 * 
	 * @param callback params : JsonResponse
	 */
	private void requestAppId(final MyCallback callback) {
		Loadings.start();

		Thread thread = new Thread(new Runnable() {

			public void run() {

				try {
					ResponseEntity<ShopApiResponse> response = restTemplate.postForEntity(URL_REQIEST_APP,
							new ShopApiRequest(), ShopApiResponse.class);
					callback.handle(response.getBody());
				} catch (Exception e) {
					e.printStackTrace();
					Dialogs.showErrorDialog("Error requesting app id: " + e.getMessage());
				} finally {
					Loadings.end();
				}
			}
		});
		thread.start();
	}

	/**
	 * 
	 * @param username
	 * @param password
	 * @param callback params : (boolean success)
	 */
	public void doLogin(final String username, final String password, final MyCallback callback) {

		Loadings.start();

		Thread thread = new Thread(new Runnable() {

			public void run() {

				boolean success = false;

				try {
					User user = User.builder().username(username).password(password).build();

					final ShopApiRequest loginRequest = ShopApiRequest.builder().user(user).build();

					ResponseEntity<HashMap> response = restTemplate.postForEntity(URL_LOGIN,
							RestComponent.buildAuthRequest(loginRequest, false), HashMap.class);

					if (response.getBody().get("code").equals("00") == false) {
						throw new InvalidActivityException("Invalid Response");
					}

					HashMap responseBody = response.getBody();
					HttpHeaders responseHeaders = response.getHeaders();
					List<String> loginKey = responseHeaders.get(HEADER_LOGIN_KEY);

					System.out.println("response: " + new ObjectMapper().writeValueAsString(responseBody));
					User responseUser = (User) MapUtil.mapToObject((Map) responseBody.get("entity"), User.class);

					AppSession.setLoginKey(loginKey.get(0));
					AppSession.setUser(responseUser);

					Dialogs.showInfoDialog("Login Success!");

					success = true;
				} catch (Exception e) {

					e.printStackTrace();
					Dialogs.showErrorDialog("Login Error: " + e.getMessage());
				} finally {

					System.out.println("Login success: " + success);

					try {
						callback.handle(success);
					} catch (Exception e) {
						System.out.println("Error calling back login");
						e.printStackTrace();
					}
				}
			}

		});
		thread.start();

	}

	/**
	 * 
	 * @param myCallback params: boolean successLogout
	 */
	public void logout(final MyCallback callback) {

		Loadings.start();

		Thread thread = new Thread(new Runnable() {

			public void run() {

				boolean success = false;

				try {

					ResponseEntity<ShopApiResponse> response = restTemplate.postForEntity(URL_LOGOUT,
							RestComponent.buildEmptyAuthRequest(true), ShopApiResponse.class);

					if (response.getBody().getCode().equals("00") == false) {
						throw new InvalidActivityException("Invalid Response: "+response.getBody().getCode());
					}
  
					AppSession.removeLoginKey(); 
					Dialogs.showInfoDialog("Logout Success!");

					success = true;
				} catch (Exception e) {

					e.printStackTrace();
					Dialogs.showErrorDialog("Logout Error: " + e.getMessage());
				} finally {

					System.out.println("Logout success: " + success); 
					try {
						callback.handle(success);
					} catch (Exception e) {
						System.out.println("Error calling back Logout");
						e.printStackTrace();
					}
				}
			}

		});

		thread.start();

	}

}
