/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.cloudstack;

import api.utils.HMACGenerator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author Alex
 */
public class CloudStackAPI {

    /**
     * @param args the command line arguments
     */
    private final String USER_AGENT = "Mozilla/5.0";
    private final String API_KEY = "3C0SgAbfNznZp2QIMy6T4sMmH24e1BK-yEHnrRpyzkh1WwzHUaGMB5-m7h6Q3HIGzE9twSWz8m-CPlYSGEmPSA";
    private final String API_SECRET = "dQ3q33e9I0g6IRFjTlyqKrM4nENXqtJxmxlqZQYlF64WXCJcS50Z2grAOPqeFs8YMpT674yINIpmP5vdWtiMMA";

    public static void main(String[] args) {
        try {
            // TODO code application logic here
            CloudStackAPI http = new CloudStackAPI();

            System.out.println("Testing 1 - Send Http GET request");
            //http.sendGet();

            System.out.println("\nTesting 2 - Send Http POST request");
            http.sendPost();
        } catch (Exception ex) {
            Logger.getLogger(CloudStackAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
// HTTP GET request

    private void sendGet() throws Exception {

        String url = "http://cs1.cloudbrix.com/client/api?";

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);

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

        String url = "http://cs1.cloudbrix.com/client/api?";

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("User-Agent", USER_AGENT);

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("command", "listNetworks"));
//        urlParameters.add(new BasicNameValuePair("account", "1"));
//        urlParameters.add(new BasicNameValuePair("diskOfferingId", "1"));
//        urlParameters.add(new BasicNameValuePair("templateId", "1"));
//        urlParameters.add(new BasicNameValuePair("zoneId", "1"));
        urlParameters.add(new BasicNameValuePair("apiKey", API_KEY));
//        urlParameters.add(new BasicNameValuePair("response", "json"));

        List<NameValuePair> orderedList = getOrderedList(urlParameters);
        String message = getMessage(orderedList);
        System.out.println("Ordered List: " + message);
        
        String signature = HMACGenerator.computeSignatureHmacSHA1(message, API_SECRET);
        System.out.println("Signature: " + signature);
        
        urlParameters.add(new BasicNameValuePair("signature", signature));
        
        printParameters(urlParameters);
        
        post.setEntity(new UrlEncodedFormEntity(urlParameters));

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

    private List<NameValuePair> getOrderedList(List<NameValuePair> unorderedList) {
        List<NameValuePair> orderedList = new ArrayList();

        Comparator<NameValuePair> comp = new Comparator<NameValuePair>() {
            @Override
            public int compare(NameValuePair p1, NameValuePair p2) {
                return p1.getName().compareTo(p2.getName());
            }
        };

        Collections.sort(unorderedList, comp);
        orderedList = unorderedList;
        return orderedList;
    }

    private String getMessage(List<NameValuePair> orderedList) {
        String message = "";
        String name = "";
        String value = "";

        for (int i = 0; i < orderedList.size(); i++) {
            name = ((NameValuePair) orderedList.get(i)).getName() + "=";
            value = ((NameValuePair) orderedList.get(i)).getValue();
            if((i+1)<orderedList.size())
                value = value + "&";
            message = message + name + value;
        }

        return message.toLowerCase();
    }
    
        private void printParameters(List<NameValuePair> parameters) {
        String message = "";
        String name = "";
        String value = "";

        for (int i = 0; i < parameters.size(); i++) {
            name = ((NameValuePair) parameters.get(i)).getName() + "=";
            value = ((NameValuePair) parameters.get(i)).getValue();
            if((i+1)<parameters.size())
                value = value + "&";
            message = message + name + value;
        }

            System.out.println("Parameters: " + message);;
    }
}
