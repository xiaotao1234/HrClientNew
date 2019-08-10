package com.huari.tools;

public class DataTools {

	public static String fourBytesToHead(byte[] b) {
		int intValue = 0;
		for (int i = 0; i < b.length; i++) {
			intValue += (b[i] & 0xFF) << (8 * (3 - i));
		}
		return Integer.toHexString(intValue);
	}

	public static long fourBytesToLong(byte[] b) {
		int intValue = 0;
		long f = 0;
		int c = (b[0] & 0xff) << 24;
		if (c < 0) {
			f = (long) (c + Math.pow(2, 32));
		} else {
			f = c;
		}
		for (int i = 1; i < b.length; i++) {
			intValue += (b[i] & 0xFF) << (8 * (3 - i));
		}
		return intValue + f;
	}
}
