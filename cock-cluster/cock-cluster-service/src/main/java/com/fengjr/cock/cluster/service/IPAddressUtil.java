package com.fengjr.cock.cluster.service;


import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

public class IPAddressUtil {

	public static String getIpAddressAndPort()
			throws MalformedObjectNameException, NullPointerException, UnknownHostException {
		MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
		Set<ObjectName> objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"),
				Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
		String host = InetAddress.getLocalHost().getHostAddress();
		String port = objectNames.iterator().next().getKeyProperty("port");
		String ipadd = "http" + "://" + host + ":" + port;
		return ipadd;
	}

	public static int getTomcatPort() throws MalformedObjectNameException, NullPointerException {
		MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
		Set<ObjectName> objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"),
				Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
		String port = objectNames.iterator().next().getKeyProperty("port");

		return Integer.valueOf(port);
	}
	
	public static String getIpAndPortString()
			throws MalformedObjectNameException, NullPointerException, UnknownHostException{
		
		MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
		Set<ObjectName> objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"),
				Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
		String host = InetAddress.getLocalHost().getHostAddress();
		String port = objectNames.iterator().next().getKeyProperty("port");
		String ipadd = host + ":" + port;
		return ipadd;
	}
}
