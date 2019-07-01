package com.fengjr.cock.common.json;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class GsonUtil {

	private static Gson gson = new GsonBuilder()
			.registerTypeAdapter(java.util.Date.class, new UtilDateSerializer())
			.registerTypeAdapter(java.util.Date.class, new UtilDateDeserializer())
			.registerTypeAdapter(java.text.DateFormat.class, new DateFormatDeserializer())
			.registerTypeAdapter(java.text.DateFormat.class, new DateFormatSerializer())
			.setDateFormat(DateFormat.LONG)
			.create();

	private static ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};
	
	public static Gson getInstance(){
		
		return gson;
	}
	
	public static Map<String, Object> getMap(Object object) throws Exception{
		
		String json = gson.toJson(object);
		return gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
	}
	
	/***
	 * @param json String
	 * @param For example, to get the type for {@code Collection<Foo>}, you should use
	 * new TypeToken<List<String>>(){}
	 * @return T
	 * */
	@SuppressWarnings("unchecked")
	public static <T> T getDomain(String json,TypeToken<T> token){
		
		return (T)gson.fromJson(json, token.getType());
	}
	
	public static JsonElement parseString(String json){
		if(json != null && json.trim().length() > 0){
			JsonParser parser = new JsonParser();
			return parser.parse(json);
		}
		return JsonNull.INSTANCE;
	}
	
	public static String[] jsonArrayToStringArray(JsonElement element) {
		
		if(element != null && element.isJsonArray()){
			
			JsonArray array = element.getAsJsonArray();
			String[] arr = new String[array.size()];
			int i = 0;
			for(JsonElement item:array){
				
				arr[i] = item.getAsString();
				i++;
			}
			
			return arr;
		}
		
		return null;
	}
	
	public static String getKeyValue(String key,JsonElement element)throws Exception{
		
		if(key != null && key.trim().length() > 0 && element != null ){
			
			JsonObject object = null;
			try{
				if(element.isJsonObject()){
					object = element.getAsJsonObject();
					element = object.get(key);
					if(element.isJsonPrimitive()){
						return element.getAsString();
					}
				}
				return element.toString();
			}
			catch(Exception e){
			}
			finally
			{
				object = null;
			}
		}
		return null;
	}
	
	public static JsonElement getKeyJsonElement(String key,JsonElement element)throws Exception{
		
		if(key != null && key.trim().length() > 0 && element != null ){
			
			JsonObject object = null;
			try{
				if(element.isJsonObject()){
					object = element.getAsJsonObject();
					element = object.get(key);
					return element;
				}
				return element;
			}
			catch(Exception e){
			}
			finally
			{
				object = null;
			}
		}
		return null;
	}
	
	public static String removeKey(String key,JsonElement element){
		
		if(key != null && key.trim().length() > 0 && element != null){
			
			JsonObject object = null;
			try{
				if(element.isJsonObject()){
					object = element.getAsJsonObject();
					object.remove(key);
					return object.toString();
				}
				return element.toString();
			}catch(Exception e){
			}
			finally
			{
				object = null;
			}
		}
		return null;
	}

	public static String toJsonString(Object o) {

		return gson.toJson(o);
	}

	public static <T> T fromJson(String json, Class<T> classOfT) {

		if(json == null || json.isEmpty()){
			return null;
		}
		
		return gson.fromJson(json, classOfT);
	}

	public static <T> T fromJson(Class<T> classOfT, String json) {
		
		if(json == null || json.isEmpty()){
			return null;
		}
		
		return gson.fromJson(json, classOfT);
	}

	private static class UtilDateDeserializer implements
			JsonDeserializer<java.util.Date> {

		public java.util.Date deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {

			if (null == json) {
				return null;
			}
			String value = json.getAsString();
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
					}else{
						date = new Date();
						date.setTime(json.getAsLong());
					}
					if (null != format) {
						date = new SimpleDateFormat(format).parse(value);
					}
				} catch (ParseException e) {
					date = new Date();
					date.setTime(json.getAsLong());
				} finally {
					value = null;
					format = null;
				}
			}
			return date;
		}
	}
	
	private static class UtilDateSerializer implements JsonSerializer<java.util.Date> {

		public JsonElement serialize(java.util.Date src, Type typeOfSrc,
				JsonSerializationContext context) {

			DateFormat format = dateFormat.get();

			return new JsonPrimitive(format.format(src));
		}
	}
	
	private static class DateFormatSerializer implements JsonSerializer<java.text.DateFormat>{

		@Override
		public JsonElement serialize(DateFormat arg0, Type arg1,
				JsonSerializationContext arg2) {
			return null;
		}
		
	}
	
	private static class DateFormatDeserializer implements JsonDeserializer<java.text.DateFormat>{

		@Override
		public DateFormat deserialize(JsonElement arg0, Type arg1,
				JsonDeserializationContext arg2) throws JsonParseException {
			return null;
		}
		
	}

}
