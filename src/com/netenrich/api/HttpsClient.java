package com.netenrich.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.common.util.Base64Utility;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpsClient {
	
	private static String SERVER_URL = "https://nealerts-ws.netenrich.net/";
	private static String AUTH_TOKEN = "f5eX98vrBXB79y6crNbquEF5nb4VjrFF"; //cbrix
	
	
	public static void main(String[] args) {
		HttpsClient restClient = new HttpsClient();
		try {

			restClient.getUsers("client_570998");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void getClientId(String varId) throws Exception {
    	HttpPost httpMethod = new HttpPost(SERVER_URL + "/api/" + varId +"/clients/filter");
        setMethodHeaders(httpMethod, varId, String.valueOf(System.currentTimeMillis()));
		StringBuffer xmlString = new StringBuffer();
		xmlString.append("<?xml version='1.0' encoding='UTF-8'?>")
			.append("<filter ver='1.0'>")
			.append("<clientName>netenrich_client1</clientName>")
			.append("</filter>");

		HttpEntity entity = new StringEntity(xmlString.toString(), "UTF-8");
		httpMethod.setEntity(entity);
		handleHttpMethod(httpMethod);
	}
	

	public void getUsers(String orgId) throws Exception {
    	HttpGet get = new HttpGet(SERVER_URL + "/api/" + orgId + "/users/");//+ "?idType=ext"
    	System.out.println(" TimeStamp " + System.currentTimeMillis() );
    	setMethodHeaders(get, orgId, String.valueOf(System.currentTimeMillis()));
        handleHttpMethod(get);
	 }

    
  
    private static void handleHttpMethod(HttpRequestBase httpMethod) throws Exception {
    	DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
        	httpclient = (DefaultHttpClient) wrapClient(httpclient);
        	HttpResponse httpResponse = httpclient.execute(httpMethod);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode ==  HttpStatus.SC_FORBIDDEN) {
                System.out.println("Authorization failure");
            } else if (statusCode ==  HttpStatus.SC_UNAUTHORIZED) {
                System.out.println("Authentication failure");
            }
			System.out.println("Status Code: "+statusCode);
			InputStream in = httpResponse.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
			}
			String reqStr = sb.toString();
			System.out.println("Response Str : " + reqStr);
			if(statusCode != HttpStatus.SC_OK) {
				String resData = "";
				Header[] headers = httpResponse.getAllHeaders();
	            for(int j=0; j<headers.length; j++) {
	            	if(headers[j].getName().equalsIgnoreCase("ERROR")) {
	            		resData = headers[j].getValue();
	            	}
	            }
	            System.out.println("ERROR: " + resData);
			}


        } finally {
            // release any connection resources used by the method
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }

    @SuppressWarnings("deprecation")
	private static HttpClient wrapClient(HttpClient base) {
    	try {
	    	SSLContext ctx = SSLContext.getInstance("TLS");
	    	X509TrustManager tm = new X509TrustManager() {

	    	public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
	    	}

	    	public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
	    	}

	    	public X509Certificate[] getAcceptedIssuers() {
	    	return null;
	    	}
	    	};
	    	ctx.init(null, new TrustManager[]{tm}, null);
	    	
	    	SSLSocketFactory ssf = new SSLSocketFactory(ctx);
	    	ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	    	ClientConnectionManager ccm = base.getConnectionManager();
	    	SchemeRegistry sr = ccm.getSchemeRegistry();
	    	sr.register(new Scheme("https", ssf, 443));
	    	return new DefaultHttpClient(ccm, base.getParams());
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		return null;
    	}
    }

    private static void setMethodHeaders(HttpRequestBase httpMethod) {
        if (httpMethod instanceof HttpPost || httpMethod instanceof HttpPut) {
        	//httpMethod.setHeader("Content-Type", MediaType.APPLICATION_XML);
        	httpMethod.setHeader("Content-Type", "text/json;charset=UTF-8");
        }
        httpMethod.setHeader("AuthToken", AUTH_TOKEN);
       // httpMethod.setHeader("Accept", MediaType.APPLICATION_XML);
        httpMethod.setHeader("Accept", MediaType.APPLICATION_JSON);
        //httpMethod.setHeader("Authorization", "Basic " + base64Encode(name + ":" + password));
    }

    private static void setMethodHeaders(HttpRequestBase httpMethod, String message, String time) {
        if (httpMethod instanceof HttpPost || httpMethod instanceof HttpPut) {
        	//httpMethod.setHeader("Content-Type", "application/xml;charset=UTF-8");
        	httpMethod.setHeader("Content-Type",  MediaType.APPLICATION_JSON);
        }
        httpMethod.setHeader("Time", time);
        httpMethod.setHeader("Hash", getHMAC(AUTH_TOKEN, message + time));
        //httpMethod.setHeader("Accept", MediaType.APPLICATION_XML);
        httpMethod.setHeader("Accept", MediaType.APPLICATION_JSON);
    }

    @SuppressWarnings("unused")
	private static String base64Encode(String value) {
        return Base64Utility.encode(value.getBytes());
        //return value;
    }

    private static String getHMAC(String key, String message) {
    	SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "HmacMD5");
        Mac mac;
        String hash = null;
 		try {
 			mac = Mac.getInstance("HmacMD5");
 	        mac.init(keySpec);
 	        byte[] rawHmac = mac.doFinal(message.getBytes());
 	        hash = Base64.encodeBytes(rawHmac);
 	        System.out.println("Hash = "+hash);
 		} catch (NoSuchAlgorithmException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (InvalidKeyException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 		return hash;
    }

 


    
}