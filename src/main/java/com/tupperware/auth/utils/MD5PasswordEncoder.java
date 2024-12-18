package com.tupperware.auth.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.crypto.password.PasswordEncoder;

public class MD5PasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(rawPassword.toString().getBytes());
			
			StringBuilder hexString = new StringBuilder();
			
			for(byte b :  digest) {
				hexString.append(String.format("%02x", b));
			}
			
			return hexString.toString();
			
		} catch (NoSuchAlgorithmException  e) {
			throw new RuntimeException("Error al crear hash MD5", e);
		}
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return encode(rawPassword).equals(encodedPassword);
	}

}
