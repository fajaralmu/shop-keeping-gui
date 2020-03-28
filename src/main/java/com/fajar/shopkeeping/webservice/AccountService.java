package com.fajar.shopkeeping.webservice;

import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_LOGIN;
import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_REQIEST_APP;

import java.util.List;

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
import com.fajar.shopkeeping.handler.AppHandler;

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

	public void getAppId(MyCallback callback) {

		requestAppId(callback);

	}

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

	public void doLogin(final String username, final String password) {

		Loadings.start();

		Thread thread = new Thread(new Runnable() {

			public void run() {

				try {
					User user = User.builder().username(username).password(password).build();

					final ShopApiRequest loginRequest = ShopApiRequest.builder().user(user).build();

					ResponseEntity<ShopApiResponse> response = restTemplate.postForEntity(URL_LOGIN,
							RestComponent.addAuthRequest(loginRequest), ShopApiResponse.class);

					if (response.getBody().getCode().equals("00") == false) {
						throw new InvalidActivityException("Invalid Response");
					}

					HttpHeaders responseHeaders = response.getHeaders();
					List<String> loginKey = responseHeaders.get("loginKey");

					AppHandler.setLoginKey(loginKey.get(0));

					Dialogs.showInfoDialog("Login Success!");
				} catch (Exception e) {

					e.printStackTrace();
					Dialogs.showErrorDialog("Login Error: " + e.getMessage());
				} finally {
				}
			}

		});
		thread.start();

	}

}
