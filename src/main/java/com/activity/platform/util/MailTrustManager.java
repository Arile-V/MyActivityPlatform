package com.activity.platform.util;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class MailTrustManager implements X509TrustManager {
    public void checkClientTrusted(X509Certificate[] cert, String authType) {
    }

    public void checkServerTrusted(X509Certificate[] cert, String authType) {
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}

