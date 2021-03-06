package com.fajar.shopkeeping.constant;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class WebServiceConstants {

	static Properties properties = null;

	public static final String WS_HOST = getWsHost();// "http://192.168.1.21:8080/universal-good-shop/";
	public static final String URL_LOGIN = WS_HOST + "api/account/login";
	public static final String URL_LOGGED_USER = WS_HOST + "api/account/user";
	public static final String URL_LOGOUT = WS_HOST + "api/account/logout";
	public static final String URL_REQIEST_APP = WS_HOST + "api/public/requestid";

	// REPORT
	public static final String URL_MONTHLY_CASHFOW = WS_HOST + "api/transaction/monthlycashflow";
	public static final String URL_DAILY_CASHFOW = WS_HOST + "api/transaction/dailycashflow";
	public static final String URL_PERIODIC_CASHFOW = WS_HOST + "api/transaction/cashflowdetail";

	// excel
	public static final String URL_REPORT_DAILY = WS_HOST + "api/report/daily";
	public static final String URL_REPORT_MONTHLY = WS_HOST + "api/report/monthly";
	public static final String URL_REPORT_ENTITY = WS_HOST + "api/report/entity";

	// ENTITY
	public static final String URL_ENTITY_GET = WS_HOST + "api/entity/get";
	public static final String URL_ENTITY_ADD = WS_HOST + "api/entity/add";
	public static final String URL_ENTITY_UPDATE = WS_HOST + "api/entity/update";

	// TRANSACTION
	public static final String URL_TRAN_SUPPLY = WS_HOST + "api/transaction/purchasing";
	public static final String URL_TRAN_SELL_V2 = WS_HOST + "api/transaction/selling";
	public static final String URL_PUBLIC_GET = WS_HOST + "api/public/get";

	private static String getWsHost() {
		if(null == properties) {
			loadProperties();
		}
		return properties.getProperty("webservice.host");
	}

	public static void loadProperties() {
		InputStream in = UrlConstants.class.getClassLoader().getResourceAsStream("application.properties");
		try {
			properties = new Properties();
			properties.load(in);
			Enumeration<Object> keys = properties.keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();
				System.out.println(key + ":" + properties.get(key));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Properties prop = new Properties();
		InputStream in = UrlConstants.class.getClassLoader().getResourceAsStream("application.properties");
		System.out.println("Reading all properties from the file");

	}

}
