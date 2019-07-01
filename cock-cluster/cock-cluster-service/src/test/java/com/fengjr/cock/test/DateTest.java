package com.fengjr.cock.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTest {

	
	public static void main(String[] args) throws Exception{
		
		DateFormat cronFormat = new SimpleDateFormat("s m H d M ? yyyy");
		
		
		Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-05-28 17:50:00");
		
		System.out.println(cronFormat.format(date));
		
		
	}
}
