package com.fajar.shopkeeping.service;

import com.fajar.entity.User;

public class AppSession {
	
	private static String applicationId = "";
	private static String loginKey = "";
	private static User loggedUser;
	
	public static void setUser(User u) {
		loggedUser = u;
	}
	
	public static void removeLoginKey() {
		loginKey = null;
		loggedUser = null;
	}
	
	public static User getUser() {
		return loggedUser;
	}
	
	public static void setApplicationID(String appId) { 
		applicationId = appId;
		System.out.println("APP ID: " + applicationId);
	}
	
	public static String getApplicationID() {
		return applicationId;
	}

	public static void setLoginKey(String loginKey2) {
		System.out.println("[Logn Key] = " + loginKey2);
		loginKey = loginKey2;
	}

	public static String getLoginKey() {
		return loginKey;
	}

}
