package com.fengjr.cock.cluster.service.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerTaskMapCache {
	
	
	private static final Map<String, Integer> cache = new ConcurrentHashMap<String, Integer>();
	
	
	public static boolean isNotEmpty(){
		
		if(cache.isEmpty()){
			
			return false;
		}
		return true;
	}
	
	public static void put(String key, Integer value) {
		
		cache.put(key, value);
	}
	
	public static Integer getCount(String key){
		
		return cache.get(key);
	}	
	
	public static synchronized void increment(String server) {
		
		Integer i = cache.get(server);
		
		if(i != null){
			cache.put(server, i+1);
		}
		else{
			cache.put(server, 1);
		}
	}
	
	public static synchronized void decrement(String server) {
		Integer i = cache.get(server);
		
		if(i != null && i.intValue() > 0){
			cache.put(server, i-1);
		}
	}
	
	public static void remove(String server){
		
		cache.remove(server);
	}
	
	public static void clear(){
		
		cache.clear();
	}
	
	public static String getMinTaskServer(){
		
		int i = Integer.MAX_VALUE;
		String server = null;
		for(Map.Entry<String, Integer> item:cache.entrySet()){
			
			if( i > item.getValue().intValue() ){
				i = item.getValue().intValue();
				server = item.getKey();
			}
		}
		return server;
	}
	
	public static int getMinTaskCount(){
		
		int i = Integer.MAX_VALUE;
		Integer count = null;
		for(Map.Entry<String, Integer> item:cache.entrySet()){
			
			if( i > item.getValue().intValue() ){
				i = item.getValue().intValue();
				count = item.getValue();
			}
		}
		return count.intValue();
	}
	
	
	public static int getMaxTaskCount(){
		
		int i = Integer.MIN_VALUE;
		Integer count = null;
		for(Map.Entry<String, Integer> item:cache.entrySet()){
			
			if( i < item.getValue().intValue() ){
				i = item.getValue().intValue();
				count = item.getValue();
			}
		}
		return count.intValue();
	}
	
	
}
