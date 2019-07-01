package com.fengjr.cock.cluster.web;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.ServletContextAware;

import com.fengjr.cock.cluster.service.zookeeper.ZKConnectedHandler;
import com.fengjr.cock.common.zookeeper.CuratorZKClient;

public class SpringContextListener implements ServletContextAware {

	
	@Value("${zookeeper.servers}")
	private String connectionString;
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		
		
		CuratorZKClient.servletContext = servletContext;
		
		CuratorZKClient client = new CuratorZKClient(connectionString, new ZKConnectedHandler());
		
		servletContext.setAttribute("client", client);
	}

}
