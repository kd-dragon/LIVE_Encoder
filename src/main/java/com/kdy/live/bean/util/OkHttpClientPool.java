package com.kdy.live.bean.util;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

@Component
public class OkHttpClientPool {
	
	private OkHttpClient pool;
	private OkHttpClient streamingMontiorPool;
	
	private OkHttpClientPool() {
		pool = new OkHttpClient().newBuilder()
				 .readTimeout(10000, TimeUnit.MILLISECONDS)
				 .retryOnConnectionFailure(false)
				 .connectTimeout(10000, TimeUnit.MILLISECONDS)
				 .connectionPool(new ConnectionPool(10, 1L,TimeUnit.MINUTES))
				 .build();
		
		streamingMontiorPool = new OkHttpClient().newBuilder()
				 .readTimeout(500, TimeUnit.MILLISECONDS)
				 .retryOnConnectionFailure(false)
				 .connectTimeout(500, TimeUnit.MILLISECONDS)
				 .connectionPool(new ConnectionPool(10, 1L,TimeUnit.MINUTES))
				 .build();
	}


	public OkHttpClient getClient() {
		return pool;
	}
	
	public OkHttpClient getStreamingMonitorClient() {
		return streamingMontiorPool;
	}

}