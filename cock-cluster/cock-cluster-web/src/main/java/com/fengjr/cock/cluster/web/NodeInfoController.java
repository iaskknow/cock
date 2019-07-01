package com.fengjr.cock.cluster.web;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fengjr.cock.cluster.service.quartz.SchedulerManager;

@Controller
public class NodeInfoController {

	
	private static final Logger log = LoggerFactory.getLogger(NodeInfoController.class);
	
	@RequestMapping ( value = "/node/stop" )
	@ResponseBody
	public Map<String, String> stop(String jobName, String groupName) {
		
		Map<String, String> map = new HashMap<String, String>();
		try{
			
			SchedulerManager.pause(jobName, groupName);
			
			map.put("SUCCESS", "true");
		}
		catch(Exception e){
			log.error("NodeInfoController stop e="+e.getMessage(), e);
			
			map.put("SUCCESS", "false");
		}
		
		return map;
	}
	
	
	@RequestMapping ( value = "/node/start" )
	@ResponseBody
	public Map<String, String> start(String jobName, String groupName) {
		
		Map<String, String> map = new HashMap<String, String>();
		try{
			SchedulerManager.resume(jobName, groupName);
			
			map.put("SUCCESS", "true");
		}
		catch(Exception e){
			log.error("NodeInfoController start e="+e.getMessage(), e);
			
			map.put("SUCCESS", "false");
		}
		
		return map;
	}
	
	
	@RequestMapping ( value = "/node/taskCount" )
	@ResponseBody
	public Map<String, String> taskCount() {
		
		Map<String, String> map = new HashMap<String, String>();
		int count = 0;
		try{
			count = SchedulerManager.getMap().size();
			map.put("COUNT", String.valueOf(count));
			map.put("SUCCESS", "true");
		}
		catch(Exception e){
			log.error("NodeInfoController taskCount e="+e.getMessage(), e);
			map.put("SUCCESS", "false");
		}
		
		return map;
	}
	
	@RequestMapping ( value = "/node/runTaskCount" )
	@ResponseBody
	public Map<String, String> runTaskCount() {
		
		Map<String, String> map = new HashMap<String, String>();
		int count = 0;
		try{
			count = SchedulerManager.getRunningTask().size();
			map.put("COUNT", String.valueOf(count));
			map.put("SUCCESS", "true");
		}
		catch(Exception e){
			log.error("NodeInfoController taskCount e="+e.getMessage(), e);
			map.put("SUCCESS", "false");
		}
		
		return map;
	}
	
	
	@RequestMapping ( value = "/node/delete" )
	@ResponseBody
	public Map<String, String> delete(String jobName, String groupName) {
		
		Map<String, String> map = new HashMap<String, String>();
		try{
			SchedulerManager.getDeleteTrigger(jobName, groupName);
			map.put("SUCCESS", "true");
		}
		catch(Exception e){
			log.error("NodeInfoController delete e="+e.getMessage(), e);
			map.put("SUCCESS", "false");
		}
		
		return map;
	}
}
