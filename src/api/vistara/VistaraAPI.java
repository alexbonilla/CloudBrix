/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.vistara;

import api.cloudstack.CloudStackAPI;
import api.utils.HMACGenerator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author Alex
 */
public class VistaraAPI {

    private final String USER_AGENT = "Mozilla/5.0";
    private final String CLIENT_ID = "client_570998";
    private final String AUTH_TOKEN = "f5eX98vrBXB79y6crNbquEF5nb4VjrFF";

//    Shit ain't working
    private void setMethodHeaders(HttpRequestBase httpMethod, String message, String time) {
        if (httpMethod instanceof HttpPost || httpMethod instanceof HttpPut) {
            httpMethod.setHeader("Content-Type", "application/xml;charset=UTF-8");
        }
        httpMethod.setHeader("Time", time);

//            httpMethod.setHeader("Hash", HMACGenerator.computeSignatureHmacMD5(message + time,AUTH_TOKEN));
        httpMethod.setHeader("Hash", HMACGenerator.getHMAC(AUTH_TOKEN, message + time));

        httpMethod.setHeader("Accept", "application/xml");
    }

    public static void main(String[] args) {
        try {
            // TODO code application logic here
            VistaraAPI http = new VistaraAPI();

            System.out.println("Testing 1 - Send Http GET request");
            http.sendGet();
            System.out.println("\nTesting 2 - Send Http POST request");
            http.sendPost();
        } catch (Exception ex) {
            Logger.getLogger(CloudStackAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
// HTTP GET request

    private void sendGet() throws Exception {

        String url = "https://app.vistarait.com/api/"+CLIENT_ID+"/devices";

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);

        setMethodHeaders(request, "", "");

        HttpResponse response = client.execute(request);

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

        String url = "https://app.vistarait.com/api/"+CLIENT_ID+"/devices";

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("User-Agent", USER_AGENT);

//        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
//        urlParameters.add(new BasicNameValuePair("command", "listNetworks"));
//        urlParameters.add(new BasicNameValuePair("account", "1"));
//        urlParameters.add(new BasicNameValuePair("diskOfferingId", "1"));
//        urlParameters.add(new BasicNameValuePair("templateId", "1"));
//        urlParameters.add(new BasicNameValuePair("zoneId", "1"));
//        urlParameters.add(new BasicNameValuePair("apiKey", API_KEY));
//        urlParameters.add(new BasicNameValuePair("response", "json"));

//        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        setMethodHeaders(post, CLIENT_ID, String.valueOf(System.currentTimeMillis()));

        HttpResponse response = client.execute(post);
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
}
