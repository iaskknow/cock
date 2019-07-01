package com.fengjr.cock.manage.service.zookeeper;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fengjr.cock.common.zookeeper.CuratorZKClient;

@Component
public class ZookeeperMangeClient {
    @Autowired
    private ServletContext servletContext;

    public  CuratorZKClient getCuratorZKClient(){

        CuratorZKClient client = null;

        if( null != servletContext ){

            client = (CuratorZKClient)servletContext.getAttribute("client");
        }
        return client;
    }
}
