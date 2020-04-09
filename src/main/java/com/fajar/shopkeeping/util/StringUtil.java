package com.fajar.shopkeeping.util;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class StringUtil {

	static final Random rand = new Random();
	
	public static final String[] months = new String[] {
		"January",
		"February",
		"March",
		"April",
		"May",
		"June",
		"July",
		"August",
		"September",
		"October",
		"November",
		"December"
	};

	public static String generateRandomNumber(int length) {

		String random = "";
		if (length < 1) {
			length = 1;
		}

		for (int i = 0; i < length; i++) {

			Integer n = rand.nextInt(9);
			random += n;
		}
		return random;
	}

	public static String[] toArrayOfString(List list) {
		
		String[] array = new String[list.size()];
		
		for (int i = 0; i < list.size(); i++) {
			try {
				array[i]  = list.get(i).toString();
			}catch (Exception e) {  }
		}
		
		return array ;
	}
	
	public static void main(String[] xxx) {

		 String[] s = new String[11];
		 s[0] = "ss";
		 s[1] = "sff";
		 s[4] = "3333";
		 System.out.println(String.join("~", s));
	}

	public static String addZeroBefore(Integer number) {
		return number < 10 ? "0" + number : number.toString();
	}

	public static String buildString(Object... strings) {

		StringBuilder stringBuilder = new StringBuilder();

		for (Object string : strings) {
			stringBuilder.append(" ").append(String.valueOf(string));
		}

		return stringBuilder.toString();
	}

	public static String doubleQuoteMysql(String str) {
		return " `".concat(str).concat("` ");
	}

	public static String beautifyNominal(Object Int) {
		if(Int == null) {
			return "0";
		}
		String[] rawNominal = Int.toString().split("\\.");
		String nominal = rawNominal[0];
		String hasil = "";
		if (nominal.length() > 3) {
			int nol = 0;
			for (int i = nominal.length() - 1; i > 0; i--) {
				nol++;
				hasil = nominal.charAt(i) + hasil;
				if (nol == 3) {
					hasil = "." + hasil;
					nol = 0;
				}

			}
			hasil = nominal.charAt(0) + hasil;
		} else {
			hasil = Int.toString();
		}

		if (rawNominal.length > 1) {
			hasil = hasil + "," + rawNominal[1];
		}

		return hasil;
	}
	
	public static String getBase64Image(File file) {
		
		try {
			String filePath;
			filePath = file.getCanonicalPath();
			String imageType = filePath.toLowerCase().endsWith("png") ? "png":"jpeg";
			
			String base64 = DatatypeConverter.printBase64Binary(Files.readAllBytes(
				    Paths.get(filePath)));
			return "data:image/"+imageType+";base64,"+base64;
		} catch (IOException e) {
			return null;
		}
		
		
	}

}
