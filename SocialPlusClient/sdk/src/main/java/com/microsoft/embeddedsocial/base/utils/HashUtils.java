/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Handy utils to get a string hash of some string
 */
public final class HashUtils {

	private static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	private HashUtils() {
	}

	public static String sha256(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(input.getBytes());
			return bytesToHex(hashBytes);
		} catch (NoSuchAlgorithmException e) {
			return "";
		}
	}

	public static String sha256Base64(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
			return Base64.encodeToString(hashBytes, Base64.NO_WRAP);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
			return "";
		}
	}

	private static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; ++i) {
			int number = bytes[i] & 0xFF;
			hexChars[i * 2] = HEX_ARRAY[number >>> 4];
			hexChars[i * 2 + 1] = HEX_ARRAY[number & 0x0F];
		}
		return new String(hexChars);
	}
}
