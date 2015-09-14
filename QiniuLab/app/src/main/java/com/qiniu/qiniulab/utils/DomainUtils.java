package com.qiniu.qiniulab.utils;

import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.http.DnspodFree;
import com.qiniu.android.dns.local.AndroidDnsServer;

import java.io.IOException;

/**
 * Created by jemy on 8/3/15.
 */
public class DomainUtils {
    private static DnsManager dnsManager;

    static {
        IResolver[] resolvers = new IResolver[2];
        resolvers[0] = AndroidDnsServer.defaultResolver();
        resolvers[1] = new DnspodFree();
        dnsManager = new DnsManager(NetworkInfo.normal, resolvers);
    }

    public static String getIpByDomain(String domain) throws IOException {
        String[] ips = dnsManager.query(domain);
        String ip = null;
        if (ips.length > 0) {
            ip = ips[0];
        }
        return ip;
    }
}
