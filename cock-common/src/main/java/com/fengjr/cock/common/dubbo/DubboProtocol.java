package com.fengjr.cock.common.dubbo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.service.GenericService;

public class DubboProtocol {
	
	
	private static final boolean ASYNC = true;
	
	/*
	 * 引用实例容器
	 * Key - 键值key
	 * Value - ReferenceConfig对象
	 */
	private static Map<String ,ReferenceConfig<GenericService>> referenceConfigMemory = new ConcurrentHashMap<String ,ReferenceConfig<GenericService>>();
	
	/*
	 * Zookeeper信息容器
	 * Key - 键值key
	 * Value - RegistryConfig对象
	 */
	private static Map<String ,RegistryConfig> registryConfigMemory = new ConcurrentHashMap<String ,RegistryConfig>();
	

	@SuppressWarnings("unchecked")
	public static <T> T get(String address, 
			String interfaze, Integer timeout, 
			String group, String version, String filter, 
			String method, String input) throws Exception
	{
		GenericService genericService = null;
		String protocol = null;
		
		try{
			if( null != interfaze && null != address && null != method && !interfaze.trim().isEmpty() && !address.trim().isEmpty() && !method.trim().isEmpty() )
			{
				address = address.trim();
				interfaze = interfaze.trim();
				method = method.trim();
				
				protocol = address.toLowerCase();
				
				if( protocol.startsWith("dubbo") )
				{
					genericService = getDirectGenericService(interfaze, address, timeout, group, version, filter);
				}
				else if( protocol.startsWith("zookeeper") )
				{
					genericService = getRegistryGenericService(interfaze, address, timeout, group, version, filter);
				}
				
				return (T)genericService.$invoke(method, new String[]{String.class.getName()}, new Object[]{input});
			}
		}
		finally
		{
			protocol = null;
			genericService = null;
		}
		return null;
	}
	
	
	
	private static GenericService getDirectGenericService(
			String interfaze, String address, Integer timeout, 
			String group, String version, String filter) throws Exception{
		ReferenceConfig<GenericService> referenceConfig = null;
		GenericService genericService = null;
		ApplicationConfig application = null;
		try{
			referenceConfig = getReferenceConfig(address + interfaze);
			if( null == referenceConfig || !referenceConfig.getUrl().equalsIgnoreCase(address) )
			{
				application = new ApplicationConfig();
				application.setName("fengjr");
				
				referenceConfig = new ReferenceConfig<GenericService>();
				referenceConfig.setApplication(application);
				referenceConfig.setUrl(address);
				referenceConfig.setInterface(interfaze);
				referenceConfig.setGeneric(true);
				referenceConfig.setTimeout(timeout);
				referenceConfig.setGroup(group);
				referenceConfig.setVersion(version);
				referenceConfig.setRetries(0);
				referenceConfig.setCheck(false);
				referenceConfig.setFilter(filter);
				referenceConfig.setAsync(ASYNC);
				setReferenceConfig(address + interfaze, referenceConfig);
			}
			
			if( null != referenceConfig ) {
				genericService = referenceConfig.get();
			}
		}
		finally
		{
			referenceConfig = null;
		}
		return genericService;
	}
	
	
	private static GenericService getRegistryGenericService(
			String interfaze, String address, Integer timeout, 
			String group, String version, String filter) throws Exception
	{
		ReferenceConfig<GenericService> referenceConfig = null;
		RegistryConfig registryConfig = null;
		List<RegistryConfig> registries = null;
		ApplicationConfig application = null;
		try{
			referenceConfig = getReferenceConfig(address + interfaze);
			
			if( null != referenceConfig )
			{
				try{
					if( !referenceConfig.getRegistries().get(0).getAddress().equalsIgnoreCase(address) )
					{
						registryConfig = new RegistryConfig();
						registryConfig.setAddress(address);
						registryConfig.setProtocol("zookeeper");
						setRegistryConfig(address, registryConfig);
						
						registries = new ArrayList<RegistryConfig>();
						registries.add(registryConfig);
						referenceConfig.setRegistries(registries);
					}
				}
				catch(Exception e)
				{
					referenceConfig = null;
				}
			}
			else
			{
				application = new ApplicationConfig();
				application.setName("fengjr");
				referenceConfig = new ReferenceConfig<GenericService>();
				referenceConfig.setApplication(application);
				referenceConfig.setInterface(interfaze);
				referenceConfig.setGeneric(true);
				referenceConfig.setTimeout(timeout);
				referenceConfig.setGroup(group);
				referenceConfig.setVersion(version);
				referenceConfig.setRetries(0);
				referenceConfig.setCheck(false);
				referenceConfig.setFilter(filter);
				referenceConfig.setAsync(ASYNC);
				
				registryConfig = getRegistryConfig(address);
				if( null == registryConfig )
				{
					registryConfig = new RegistryConfig();
					registryConfig.setAddress(address);
					registryConfig.setProtocol("zookeeper");
					setRegistryConfig(address, registryConfig);
				}
				
				registries = new ArrayList<RegistryConfig>();
				registries.add(registryConfig);
				referenceConfig.setRegistries(registries);
				setReferenceConfig(address + interfaze, referenceConfig);
			}
			if( null != referenceConfig ) return referenceConfig.get();
		}
		finally
		{
			referenceConfig = null;
			registryConfig = null;
			registries = null;
		}
		return null;
	}
	
	
	
	
	/**
	 * 设置引用实例
	 * @param key 					- 键值
	 * @param referenceConfig	- 引用实例对象
	 * @throws Exception			- 捕捉异常
	 */
	private static void setReferenceConfig(String key ,final ReferenceConfig<GenericService> referenceConfig) throws Exception
	{
		if( null != key && !key.trim().isEmpty() && null != referenceConfig )
		{
			key = key.trim();
			if( referenceConfigMemory.containsKey(key) )
			{
				ReferenceConfig<GenericService> referenceConfigOld = null;
				try{
					referenceConfigOld = referenceConfigMemory.get(key);
					if( null != referenceConfigOld ) referenceConfigOld.destroy();
					referenceConfigOld = null;
				}
				finally
				{
					if( null != referenceConfigOld ) referenceConfigOld.destroy();
					referenceConfigOld = null;
				}
			}
			referenceConfigMemory.put(key, referenceConfig);
		}
	}
	
	/**
	 * 获取引用实例
	 * @param key 					- 键值
	 * @return ReferenceConfig	- 引用实例对象
	 * @throws Exception			- 捕捉异常
	 */
	private static ReferenceConfig<GenericService> getReferenceConfig(final String key) throws Exception
	{
		if( null != key && !key.trim().isEmpty() && referenceConfigMemory.containsKey(key.trim()) )
		{
			return referenceConfigMemory.get(key.trim());
		}
		return null;
	}
	
	
	/**
	 * 设置SAF服务Zookeeper信息
	 * @param key 				- 键值
	 * @param registryConfig	- Zookeeper信息对象
	 * @throws Exception		- 捕捉异常
	 */
	private static void setRegistryConfig(final String key ,final RegistryConfig registryConfig) throws Exception
	{
		if( null != key && !key.trim().isEmpty() && null != registryConfig )
		{
			registryConfigMemory.put(key.trim(), registryConfig);
		}
	}
	
	/**
	 * 获取DUBBO服务Zookeeper信息
	 * @param key 					- 键值
	 * @return RegistryConfig	- Zookeeper信息对象
	 * @throws Exception			- 捕捉异常
	 */
	private static RegistryConfig getRegistryConfig(final String key) throws Exception
	{
		if( null != key && !key.trim().isEmpty() && registryConfigMemory.containsKey(key.trim()) )
		{
			return registryConfigMemory.get(key.trim());
		}
		return null;
	}
	
	public static void destroy(String key) throws Exception{
		
		if( null != key && !key.trim().isEmpty() )
		{
			key = key.trim();
			if( referenceConfigMemory.containsKey(key) )
			{
				ReferenceConfig<GenericService> referenceConfigOld = null;
				try{
					referenceConfigOld = referenceConfigMemory.get(key);
					if( null != referenceConfigOld ) referenceConfigOld.destroy();
					referenceConfigOld = null;
				}
				finally
				{
					if( null != referenceConfigOld ) referenceConfigOld.destroy();
					referenceConfigOld = null;
				}
			}
			referenceConfigMemory.remove(key);
		}
	}
	
	
}
