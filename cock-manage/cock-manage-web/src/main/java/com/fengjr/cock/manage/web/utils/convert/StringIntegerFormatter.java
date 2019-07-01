package com.fengjr.cock.manage.web.utils.convert;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

public class StringIntegerFormatter implements Formatter<Integer>{

	@Override
	public String print(Integer arg0, Locale arg1) {
		return String.valueOf(arg0);
	}

	@Override
	public Integer parse(String arg0, Locale arg1) throws ParseException {
		
		if(arg0 == null || arg0.trim().equals("")){
			arg0 = "-1";
		}
		
		return Integer.valueOf(arg0);
	}

}
