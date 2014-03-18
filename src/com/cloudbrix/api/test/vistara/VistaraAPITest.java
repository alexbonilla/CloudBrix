/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cloudbrix.api.test.vistara;

import com.cloudbrix.utils.HMACGenerator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;




/**
 *
 * @author Alex
 */
public class VistaraAPITest {

    private final String USER_AGENT = "Mozilla/5.0";
    private final String CLIENT_ID = "client_570998";
    private final String AUTH_TOKEN = "f5eX98vrBXB79y6crNbquEF5nb4VjrFF";

    private void setMethodHeaders(HttpRequestBase httpMethod, String message, String time) {
        if (httpMethod instanceof HttpPost || httpMethod instanceof HttpPut) {
            httpMethod.setHeader("Content-Type", "application/xml;charset=UTF-8");
        }
        httpMethod.setHeader("Time", time);
//        try {
//            httpMethod.setHeader("Hash", HMACGenerator.computeSignatureHmacMD5(message + time,AUTH_TOKEN));
//        } catch (GeneralSecurityException ex) {
//            Logger.getLogger(VistaraAPITest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(VistaraAPITest.class.getName()).log(Level.SEVERE, null, ex);
//        }
        httpMethod.setHeader("Hash", HMACGenerator.getHMAC(AUTH_TOKEN, message + time));

        httpMethod.setHeader("Accept", "application/xml");
    }

    public static void main(String[] args) {
        try {
            // TODO code application logic here
            VistaraAPITest http = new VistaraAPITest();

            System.out.println("Testing 1 - Send Http GET request");
            http.sendGet();
            System.out.println("\nTesting 2 - Send Http POST request");
            http.sendPost();
        } catch (Exception ex) {
            System.out.println("ERROR " + ex.toString());
        }
    }
// HTTP GET request

    private void sendGet() throws Exception {
//        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//        FileInputStream instream = new FileInputStream(new File("C:\\Users\\Alex\\Documents\\CloudBrix\\myab.keystore"));
//        try {
//            trustStore.load(instream, "123432".toCharArray());
//            trustStore.load(null, null);
//        } finally {
//            instream.close();
//        }
//        SSLContext sslcontext = SSLContexts.custom()
//                //                .loadTrustMaterial(trustStore)
//                .build();
//
//        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
//                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//        CloseableHttpClient httpclient = HttpClients.custom()
//                .setSSLSocketFactory(sslsf)
//                .build();

        HttpClient httpclient = wrapClient();

        String url = "https://nealerts-ws.netenrich.net/" + CLIENT_ID + "/devices";

        HttpGet request = new HttpGet(url);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);

        setMethodHeaders(request, CLIENT_ID, String.valueOf(System.currentTimeMillis()));

        HttpResponse response = httpclient.execute(request);

        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        System.out.println(result.toString());

    }

    // HTTP POST request
    private void sendPost() throws Exception {
//        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//        FileInputStream instream = new FileInputStream(new File("C:\\Users\\Alex\\Documents\\CloudBrix\\myab.keystore"));
//        try {
//            trustStore.load(instream, "123432".toCharArray());
//        } finally {
//            instream.close();
//        }
//        SSLContext sslcontext = SSLContexts.custom()
//                .loadTrustMaterial(trustStore)
//                .build();
//
//        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
//                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
//        CloseableHttpClient httpclient = HttpClients.custom()
//                .setSSLSocketFactory(sslsf)
//                .build();

        HttpClient httpclient = wrapClient();

        String url = "https://nealerts-ws.netenrich.net/" + CLIENT_ID + "/devices";

        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("User-Agent", USER_AGENT);

        setMethodHeaders(post, CLIENT_ID, String.valueOf(System.currentTimeMillis()));

        HttpResponse response = httpclient.execute(post);
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + post.getEntity());
        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        System.out.println(result.toString());

    }

    @SuppressWarnings("deprecation")
    private static HttpClient wrapClient() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(ctx, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            return httpclient;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
