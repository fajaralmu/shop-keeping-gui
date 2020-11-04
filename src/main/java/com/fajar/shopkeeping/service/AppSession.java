package com.fajar.shopkeeping.service;

import com.fajar.shoppingmart.entity.Profile;
import com.fajar.shoppingmart.entity.User;

public class AppSession {
	
	private static String applicationId = "1234";
	private static String loginKey = "";
	private static User loggedUser;
	private static Profile applicationProfile = defaultProfile();
	
	public static void setApplicationProfile(Profile profile) {
		applicationProfile = profile;
	}
	
	private static Profile defaultProfile() {
		 
		return Profile.builder().name("ECommerce System").address("-").contact("-").about("-").build();
	}

	public static Profile getApplicationProfile() {
		return applicationProfile;
	}
	
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

	//TODO: parameterize
	public static String getDeveloperContact() { 
		return "somabangsa@gmail.com";
	}

}
