package com.tupperware.utils;

public class ValidationUtil {

	public static boolean isEmail(String email) {
		String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
		return email.matches(emailRegex);
	}
}
