package com.qiniu.qiniulab.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class Tools {
    private final static int FZ_KB = 1024;
    private final static int FZ_MB = 1024 * FZ_KB;
    private final static int FZ_GB = 1024 * FZ_MB;
    private final static int FZ_PB = 1024 * FZ_GB;

    private final static int TS_SECOND = 1000;
    private final static int TS_MINUTE = 60 * TS_SECOND;
    private final static int TS_HOUR = 60 * TS_MINUTE;

    public static String formatSize(long fileLength) {
        StringBuilder sb = new StringBuilder();
        if (fileLength < FZ_KB) {
            sb.append(formatDouble(fileLength, 1)).append(" B");
        } else if (fileLength <= FZ_MB) {
            sb.append(formatDouble(fileLength, FZ_KB)).append(" KB");
        } else if (fileLength <= FZ_GB) {
            sb.append(formatDouble(fileLength, FZ_MB)).append(" MB");
        } else if (fileLength <= FZ_PB) {
            sb.append(formatDouble(fileLength, FZ_GB)).append(" GB");
        } else {
            sb.append(formatDouble(fileLength, FZ_PB)).append(" PB");
        }
        return sb.toString();
    }

    public static String formatMilliSeconds(long milliSeconds) {
        StringBuilder sb = new StringBuilder();
        long left = milliSeconds;
        if (left / TS_HOUR > 0) {
            sb.append(left / TS_HOUR).append("h ");
            left -= (left / TS_HOUR) * TS_HOUR;
        }
        if (left / TS_MINUTE > 0) {
            sb.append(left / TS_MINUTE).append("m ");
            left -= (left / TS_MINUTE) * TS_MINUTE;
        }
        if (left / TS_SECOND > 0) {
            sb.append(left / TS_SECOND).append("s ");
            left -= (left / TS_SECOND) * TS_SECOND;
        }
        sb.append(left).append("ms ");
        return sb.toString();
    }

    public static String formatDouble(long value, int divider) {
        double result = value * 1.0 / divider;
        return String.format(Locale.getDefault(), "%.2f", result);
    }

    public static String formatSpeed(double deltaSize, double deltaMillis) {
        double speed = deltaSize * 1000 / deltaMillis / FZ_KB;
        String result = String.format(Locale.getDefault(), "%.2f KB/s", speed);
        if ((int) speed > FZ_KB) {
            result = String.format(Locale.getDefault(), "%.2f MB/s", speed
                    / FZ_KB);
        }
        return result;
    }

    public static byte[] sha1(String val) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        byte[] data = val.getBytes("utf-8");
        MessageDigest mDigest = MessageDigest.getInstance("sha1");
        return mDigest.digest(data);
    }

}
