package com.fengjr.cock.manage.service.util;

import java.io.File;
import java.util.List;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.commons.collections.map.ListOrderedMap;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.fengjr.page.mybatis.service.LogCommonService;



@SuppressWarnings("unchecked")
public class ViewTools {

	private static Element node = null;
	private static Map<String,Map<String,Map<String,String>>> map = null;
	static{
		SAXReader reader = null;
		Document document = null;
		List<Element> list = null;
		List<Element> subList = null;
        try{
			reader = new SAXReader();  
	        document = reader.read(new File(ViewTools.class.getResource("/enum.xml").getFile()));
	        node = document.getRootElement();  
	        list = node.elements();
	        Map<String,Map<String,String>> subMap = null;
	        Map<String,String> cMap = null;
	        map = new ListOrderedMap();
	        for(Element e:list){
	        	subList = e.elements();
	        	subMap = new ListOrderedMap();
	        	for(Element item:subList){
	        		
	        		cMap = new ListOrderedMap();
	        		cMap.put("name", item.attributeValue("name"));
	        		cMap.put("value", item.attributeValue("value"));
	        		cMap.put("text", item.getText());
	        		
	        		subMap.put(item.attributeValue("name"), cMap);
	        	}
	        	map.put(e.attributeValue("name"),subMap);
	        }
	        
        }catch(Exception e){
        	LogCommonService.error(ViewTools.class, "[ViewTools static]", e);
        }
	}
	
	
	public static String getValue(String name){
		
		if(name == null || name.isEmpty()){
			return null;
		}
		
		StringBuilder s = new StringBuilder();
		s.append(name).append(".value");
		
		try {
			return String.valueOf(Ognl.getValue(s.toString(), map));
		} catch (OgnlException e) {
			LogCommonService.error(ViewTools.class, "[ViewTools getValue]", e);
		}
		finally{
			s = null;
		}
		return null;
	}
	
	public static String getValue(String name, String text){
		
		if(name == null || text == null || name.isEmpty() || text.isEmpty()){
			return null;
		}
		
		String value = null;
		
		try{
			
			Map<String,Map<String,String>> options = (Map<String,Map<String,String>>)Ognl.getValue(name, map);
			
			for(Map.Entry<String,Map<String,String>> entry:options.entrySet()){
				if(entry.getValue().get("text").equals(text)){
					value = entry.getValue().get("value");
					break;
				}
			}
			
		}catch(OgnlException e){
			LogCommonService.error(ViewTools.class, "[ViewTools getValue text]", e);
		}
		return value;
		
	}
	
	public static String getText(String name){
		
		if(name == null || name.isEmpty()){
			return null;
		}
		
		StringBuilder s = new StringBuilder();
		s.append(name).append(".text");
		
		try {
			return String.valueOf(Ognl.getValue(s.toString(), map));
		} catch (OgnlException e) {
			LogCommonService.error(ViewTools.class, "[ViewTools getText]", e);
		}
		finally{
			s = null;
		}
		return null;
	}
	
	public static String getText(String name, Integer value){
		
		return getText(name, String.valueOf(value));
	}
	
	public static String getText(String name, String value){
		
		if(name == null || value == null || name.isEmpty() || value.isEmpty()){
			return null;
		}
		
		String text = value;
		
		try{
			
			Map<String,Map<String,String>> options = (Map<String,Map<String,String>>)Ognl.getValue(name, map);
			
			for(Map.Entry<String,Map<String,String>> entry:options.entrySet()){
				if(entry.getValue().get("value").equals(value)){
					text = entry.getValue().get("text");
					break;
				}
			}
			
		}catch(OgnlException e){
			LogCommonService.error(ViewTools.class, "[ViewTools getText value]", e);
		}
		return text;
	}
	
	public static String getOptions(String name){
		
		Map<String,String> value = null;
		StringBuilder select = new StringBuilder();
		Map<String,Map<String,String>> options = map.get(name);
		for(Map.Entry<String, Map<String,String>> entry:options.entrySet()){
			
			value = entry.getValue();
			
			select.append("<option value=\"").append(value.get("value")).append("\"").append(">");
			select.append(value.get("text")).append("</option> \r\n");
		}
		return select.toString();
	}
	
	public static String getOptionsCheck(String name, Integer check){
		
		return getOptionsCheck(name, String.valueOf(check));
	}
	
	public static String getOptionsCheck(String name, String check){
		
		Map<String,String> value = null;
		StringBuilder select = new StringBuilder();
		
		Map<String,Map<String,String>> options = map.get(name);
		for(Map.Entry<String, Map<String,String>> entry:options.entrySet()){
			
			value = entry.getValue();
			if(value.get("value").equals(check)){
				select.append("<option value=\"").append(value.get("value")).append("\"").append(" selected >");
			}else{
				select.append("<option value=\"").append(value.get("value")).append("\"").append(">");
			}
			select.append(value.get("text")).append("</option> \r\n");
		}
		
		return select.toString();
	}
	
	public static String getSelect(String name){
		
		Map<String,String> value = null;
		StringBuilder select = new StringBuilder();
		select.append("<select name=\"").append(name).append("\">\r\n");
		
		Map<String,Map<String,String>> options = map.get(name);
		for(Map.Entry<String, Map<String,String>> entry:options.entrySet()){
			
			value = entry.getValue();
			
			select.append("<option value=\"").append(value.get("value")).append("\"").append(">");
			select.append(value.get("text")).append("</option> \r\n");
		}
		select.append("</select>\r\n");
		
		return select.toString();
	}
	
	public static String getSelectCheck(String name, String check){
		
		Map<String,String> value = null;
		StringBuilder select = new StringBuilder();
		select.append("<select name=\"").append(name).append("\">\r\n");
		
		Map<String,Map<String,String>> options = map.get(name);
		for(Map.Entry<String, Map<String,String>> entry:options.entrySet()){
			
			value = entry.getValue();
			if(value.get("value").equals(check)){
				select.append("<option value=\"").append(value.get("value")).append("\"").append(" selected >");
			}else{
				select.append("<option value=\"").append(value.get("value")).append("\"").append(">");
			}
			select.append(value.get("text")).append("</option> \r\n");
		}
		select.append("</select>\r\n");
		
		return select.toString();
	}
}
