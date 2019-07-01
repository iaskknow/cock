package com.fengjr.cock.common.zookeeper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ZKManager {

	
	public static void main(String[] args) {
		
		
		Date date = new Date(1554892377000L);
		
		System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(date));
		
	}
	
	
}
