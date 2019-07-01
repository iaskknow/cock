package com.fengjr.cock.manage.service.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MoneyFormatTools {

	private static DecimalFormat format = new DecimalFormat("#,##0.###");
	
	public static String moneyFormat(Object input){
		if(input == null){
			return "";
		}
		String str = input.toString();
		
		return moneyFormat(str);
	}
	
	public static String moneyFormat(String input) {
		
		if(input == null || input.trim().equals("")){
			return "";
		}
		
		BigDecimal money = new BigDecimal(input);
		
		return moneyFormat(money);
	}
	
	public static String moneyFormat(BigDecimal input) {
		
		if(input == null){
			return "";
		}
		
		return format.format(input);
	}
}
