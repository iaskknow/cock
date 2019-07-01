package com.fengjr.cock.manage.web.listener;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.ServletContextAware;

import com.fengjr.cock.common.zookeeper.CuratorZKClient;
import com.fengjr.cock.manage.service.zookeeper.ZKConnectedHandler;

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
