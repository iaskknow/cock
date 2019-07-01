package com.fengjr.cock.manage.service.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fengjr.page.mybatis.service.LogCommonService;

public class DateFormatTools {

	
	private static ThreadLocal<SimpleDateFormat> local = new ThreadLocal<SimpleDateFormat>(){
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};
	
	
	public static String formatDate(Date date){
		
		if(date == null){
			return null;
		}
		
		return local.get().format(date);
	}
	
	public static String formatDate(Date date, String pattern){
		
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		
		return format.format(date);
	}
	
	public static Date formatDate(String value){
		
		Date date = null;
		if ((null != value) && (!value.trim().equals(""))
				&& (!value.trim().equals("null"))) {
			value = value.trim();
			String format = "yyyy-MM-dd HH:mm:ss";
			try {
				
				if (value.contains("-")) {
					if (value.length() == 19) {
						format = "yyyy-MM-dd HH:mm:ss";
					} else if (value.length() == 16) {
						format = "yyyy-MM-dd HH:mm";
					} else if (value.length() == 13) {
						format = "yyyy-MM-dd HH";
					} else if (value.length() == 10) {
						format = "yyyy-MM-dd";
					}
				} else if (value.contains(":")) {
					if (value.length() == 8) {
						format = "HH:mm:ss";
					} else if (value.length() == 5) {
						format = "HH:mm";
					}
				} else if (value.length() == 2) {
					format = "HH";
				} else if (value.length() == 4) {
					format = "HHmm";
				} else if (value.length() == 6) {
					format = "HHmmss";
				} else if (value.length() == 8) {
					format = "yyyyMMdd";
				} else if (value.length() == 10) {
					format = "yyyyMMddHH";
				} else if (value.length() == 12) {
					format = "yyyyMMddHHmm";
				} else if (value.length() == 14) {
					format = "yyyyMMddHHmmss";
				}
				if (null != format) {
					date = new SimpleDateFormat(format).parse(value);
				}
			}
			catch(ParseException e){
				LogCommonService.error(DateFormatTools.class, "ParseException[DateFormatTools convert]value="+value, e);
			}
			catch(Exception e){
				LogCommonService.error(DateFormatTools.class, "Exception[DateFormatTools convert]value="+value, e);
			}
			finally{
				format = null;
			}
		}
		return date;
	}
	
	
}
