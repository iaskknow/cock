package com.fengjr.cock.common.domain.enums;

public enum ScheduleJobStatusEnum {

	
	STATE_NORMAL(0,"正常"),
	STATE_PAUSED(1,"暂停");
	
	
	ScheduleJobStatusEnum(int code, String discription){
		this.code = code;
		this.discription = discription;
	}
	
	
	private int code;
	
	private String discription;

	public int getCode() {
		return code;
	}

	public String getDiscription() {
		return discription;
	}
	
	
}
