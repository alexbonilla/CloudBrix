/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cloudbrix.utils;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 *
 * @author Alex
 */
public class MyHttpClientWrapper  implements X509TrustManager{
    


                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

}
