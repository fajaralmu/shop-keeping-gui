package com.fajar.shopkeeping.constant;

public class WebServiceConstants {

	public static final String WS_HOST = "http://localhost:8080/universal-good-shop/";
	public static final String URL_LOGIN = WS_HOST + "api/account/login";
	public static final String URL_LOGOUT = WS_HOST + "api/account/logout";
	public static final String URL_REQIEST_APP = WS_HOST + "api/public/requestid";
	
	//REPORT
	public static final String URL_MONTHLY_CASHFOW = WS_HOST + "api/transaction/monthlycashflow";
}
