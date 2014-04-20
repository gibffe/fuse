package com.sulaco.fuse;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Ignore;

@Ignore
public class TestUtil {

	private static HttpClient client;
	
	public static HttpClient getHttpClient() {
		if (client == null) {
			initHttpClient();
		}
		return client;
	}
	
	private static void initHttpClient() {
		client = HttpClients.createDefault();
	}
	
}
