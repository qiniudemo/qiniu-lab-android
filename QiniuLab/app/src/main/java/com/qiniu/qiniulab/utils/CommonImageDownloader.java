package com.qiniu.qiniulab.utils;


import android.content.Context;

import com.nostra13.universalimageloader.core.assist.ContentLengthInputStream;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.IoUtils;
import com.qiniu.android.dns.DnsManager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by bailong on 15/8/17.
 */
public class CommonImageDownloader extends BaseImageDownloader {
    private final DnsManager dns;

    public CommonImageDownloader(Context context, int connectTimeout, int readTimeout, DnsManager dns) {
        super(context, connectTimeout, readTimeout);
        this.dns = dns;
    }

    private static boolean serverError(HttpURLConnection conn) throws IOException {
        return conn.getResponseCode() / 100 == 5;
    }

    private static boolean validIP(String ip) {
        if (ip == null || ip.length() < 7 || ip.length() > 15) return false;
        if (ip.contains("-")) return false;

        try {
            int x = 0;
            int y = ip.indexOf('.');

            if (y != -1 && Integer.parseInt(ip.substring(x, y)) > 255) return false;

            x = ip.indexOf('.', ++y);
            if (x != -1 && Integer.parseInt(ip.substring(y, x)) > 255) return false;

            y = ip.indexOf('.', ++x);
            return !(y != -1 && Integer.parseInt(ip.substring(x, y)) > 255 &&
                    Integer.parseInt(ip.substring(++y, ip.length() - 1)) > 255 &&
                    ip.charAt(ip.length() - 1) != '.');

        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Retrieves {@link InputStream} of image by URI (image is located in the network).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link InputStream} of image
     * @throws IOException if some I/O error occurs during network request or if no InputStream could be created for
     *                     URL.
     */
    protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
        URL u = new URL(imageUri);
        String host = u.getHost();
        String[] ips = resolveAddresses(host);
        IOException lastException = null;
        for (String ip : ips) {
            HttpURLConnection conn = createConnection(imageUri, extra);
            conn.setRequestProperty("Host", host);
            int redirectCount = 0;
            try {
                while (conn.getResponseCode() / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT) {
                    conn = createConnection(conn.getHeaderField("Location"), extra);
                    redirectCount++;
                }
            } catch (IOException e) {
                lastException = e;
                continue;
            }

            InputStream imageStream;
            try {
                imageStream = conn.getInputStream();
            } catch (IOException e) {
                // Read all data to allow reuse connection (http://bit.ly/1ad35PY)
                IoUtils.readAndCloseStream(conn.getErrorStream());
                lastException = e;
                continue;
            }
            if (serverError(conn)) {
                lastException = new IOException("Image request failed with response code " + conn.getResponseCode());
                continue;
            }
            if (!shouldBeProcessed(conn)) {
                IoUtils.closeSilently(imageStream);
                throw new IOException("Image request failed with response code " + conn.getResponseCode());
            }

            return new ContentLengthInputStream(new BufferedInputStream(imageStream, BUFFER_SIZE), conn.getContentLength());
        }
        if (lastException != null) {
            throw lastException;
        } else {
            throw new IOException("unexpect error");
        }

    }

    private String replaceHost(String url, String host, String ip) {
        return url.replaceFirst(host, ip);
    }

    private String[] resolveAddresses(String domain) throws IOException {
        if (dns == null) {
            InetAddress[] addresses = InetAddress.getAllByName(domain);
            String[] x = new String[addresses.length];
            for (int i = 0; i < addresses.length; i++) {
                x[i] = addresses[i].getHostAddress();
            }
            return x;
        }
        try {
            return dns.query(domain);
        } catch (IOException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }
}