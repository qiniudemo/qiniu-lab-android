package com.qiniu.qiniulab.utils;

import java.util.Locale;

public class Tools {
	private final static int FZ_KB = 1024;
	private final static int FZ_MB = 1024 * FZ_KB;
	private final static int FZ_GB = 1024 * FZ_MB;
	private final static int FZ_PB = 1024 * FZ_GB;

	public static String formatSize(long fileLength) {
		StringBuilder sb = new StringBuilder();
		if (fileLength < FZ_KB) {
			sb.append(formatDouble(fileLength, 1)).append(" B");
		} else if (fileLength < FZ_MB) {
			sb.append(formatDouble(fileLength, FZ_KB)).append(" KB");
		} else if (fileLength < FZ_GB) {
			sb.append(formatDouble(fileLength, FZ_MB)).append(" MB");
		} else if (fileLength < FZ_PB) {
			sb.append(formatDouble(fileLength, FZ_GB)).append(" GB");
		} else {
			sb.append(formatDouble(fileLength, FZ_PB)).append(" PB");
		}
		return sb.toString();
	}

	private static String formatDouble(long fileLength, int divider) {
		double result = fileLength * 1.0 / divider;
		return String.format(Locale.getDefault(), "%.2f", result);
	}
}
