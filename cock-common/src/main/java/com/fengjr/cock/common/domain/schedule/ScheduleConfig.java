package com.fengjr.cock.common.domain.schedule;

import com.fengjr.cock.common.domain.dubbo.DubboConfig;
import com.fengjr.cock.common.domain.http.HttpConfig;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScheduleConfig {

	private String id;
	// CRON/SIMPLE
	private String scheduleType;
	// DUBBO/HTTP
	private String protocolType;
	// 显示名称
	private String taskName     = null;
	// 用户表示
	private String userId		= null;
	// CRON表达式
    private String jobCron      = null;
    // 开始执行时间
    private Date startTime    	= null;
    // 结束时间
    private Date stopTime    	= null;
    // 执行间隔(秒)
    private long jobInterval    = -1;
    // 执行次数-1无限制
    private int jobCount        = -1;
    // 任务状态
    private String jobStatus 	= null;
    // 任务参数
    private String jobParams    = null;
    // 加载编号
    private String loadId       = null;
    // 加载时间
    private Date loadTime       = null;
    // 创建时间
    private Date creationDate   = null;
    // 更新时间
    private Date lastUpdateDate = null;
    //dubbo相关参数
	private DubboConfig dubboConfig;
    //http相关参数
	private HttpConfig httpConfig;

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public DubboConfig getDubboConfig() {
		return dubboConfig;
	}

	public void setDubboConfig(DubboConfig dubboConfig) {
		this.dubboConfig = dubboConfig;
	}

	public HttpConfig getHttpConfig() {
		return httpConfig;
	}

	public void setHttpConfig(HttpConfig httpConfig) {
		this.httpConfig = httpConfig;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getScheduleType() {
		return scheduleType;
	}
	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}
	public String getProtocolType() {
		return protocolType;
	}
	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}
	public String getJobCron() {
		return jobCron==null?"":jobCron;
	}
	public void setJobCron(String jobCron) {
		this.jobCron = jobCron;
	}
	public long getJobInterval() {
		return jobInterval*1000L;
	}
	public void setJobInterval(long jobInterval) {
		this.jobInterval = jobInterval;
	}
	public int getJobCount() {
		return jobCount;
	}
	public void setJobCount(int jobCount) {
		this.jobCount = jobCount;
	}
	public String getJobParams() {
		return jobParams;
	}
	public void setJobParams(String jobParams) {
		this.jobParams = jobParams;
	}
	public String getLoadId() {
		return loadId;
	}
	public void setLoadId(String loadId) {
		this.loadId = loadId;
	}
	public Date getLoadTime() {
		return loadTime;
	}
	public void setLoadTime(Date loadTime) {
		this.loadTime = loadTime;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public String getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Date getStartTime() {
		return startTime==null ? Calendar.getInstance().getTime() : startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getStopTime() {
		return stopTime;
	}
	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
	}
    
	public String getStartTimeString(){
		
		if(null != startTime){
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime);
		}
		
		return "";
	}
	
	public String getStopTimeString(){
		
		if(null != stopTime){
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(stopTime);
		}
		
		return "";
	}
	
	public String getGroupName() {
		return "FENGJR_COCK_GROUP_" + this.userId;
	}
	
	public String getJobName() {
		return "JOB_"+id;
	}
}
