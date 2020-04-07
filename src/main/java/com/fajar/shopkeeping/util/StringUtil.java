package com.fajar.shopkeeping.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

import com.fajar.shopkeeping.component.ComponentBuilder;

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

	public static void main(String[] xxx) {

		for (int i = 1; i <= 611; i++) {

			System.out.println("update `transaction` set code= '" + generateRandomNumber(10) + "' where id=" + i + ";");
		}
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

	public static String beautifyNominal(Long Int) {
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
