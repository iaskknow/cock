package com.fengjr.cock.common.domain.schedule;

import java.util.HashMap;
import java.util.Map;

public class AllotDomain {

	private String leader;
	
	// 0 分配中  1分配完成
	private String status;
	
	private Map<String, String> loadMap = new HashMap<String, String>();
	
	public String getLeader() {
		return leader;
	}

	public void setLeader(String leader) {
		this.leader = leader;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Map<String, String> getLoadMap() {
		return loadMap;
	}

	public void setLoadMap(Map<String, String> loadMap) {
		this.loadMap = loadMap;
	}

}
