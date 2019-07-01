package com.fengjr.cock.cluster.web.convert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.format.Formatter;

import com.fengjr.page.mybatis.service.LogCommonService;

public class DateStringFormatter implements Formatter<Date> {

	@Override
	public String print(Date object, Locale locale) {
			
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(object);
	}

	@Override
	public Date parse(String value, Locale locale) throws ParseException {
		
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
				LogCommonService.error(DateStringFormatter.class, "ParseException[StringToDateConverter convert]value="+value, e);
			}
			catch(Exception e){
				LogCommonService.error(DateStringFormatter.class, "Exception[StringToDateConverter convert]value="+value, e);
			}
			finally{
				format = null;
			}
		}
		return date;
	}


}
