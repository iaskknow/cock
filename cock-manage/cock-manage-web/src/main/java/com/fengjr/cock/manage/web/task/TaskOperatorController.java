package com.fengjr.cock.manage.web.task;

import com.fengjr.cock.common.domain.dubbo.DubboConfig;
import com.fengjr.cock.common.domain.enums.HttpMethodEnum;
import com.fengjr.cock.common.domain.enums.ProtocolTypeEnum;
import com.fengjr.cock.common.domain.enums.ScheduleTypeEnum;
import com.fengjr.cock.common.domain.http.HttpConfig;
import com.fengjr.cock.common.domain.schedule.ScheduleConfig;
import com.fengjr.cock.common.http.HttpProtocol;
import com.fengjr.cock.common.json.GsonUtil;
import com.fengjr.cock.common.zookeeper.CuratorZKClient;
import com.fengjr.cock.common.zookeeper.ZKDictionary;
import com.fengjr.cock.manage.service.zookeeper.ZookeeperMangeClient;
import com.fengjr.cock.manage.web.utils.convert.ResultJson;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;



@RequestMapping("/cock")
@Controller
public class TaskOperatorController {
    protected static final Logger log = LoggerFactory.getLogger(TaskOperatorController.class);
    @Autowired
    private ZookeeperMangeClient zookeeperMangeClient;


    @RequestMapping("/deleteTask")
    @ResponseBody
    public ResultJson deleteTask(@RequestParam(value = "id", required = false) String id) {
        ResultJson resultJson = new ResultJson();
        String taskPath = null;
        String configData = null;
        CuratorZKClient curatorZKClient = null;
        ScheduleConfig scheduleConfig = null;
        String uri = null;
        String jobName = null;
        String groupName = null;
        Map<String,String> httpMap = null;
        String httpResult = null;
        try {

            taskPath = ZKDictionary.INSTANCE.getTaskPath() + "/" + id;
            curatorZKClient = zookeeperMangeClient.getCuratorZKClient();
            configData = curatorZKClient.getData(taskPath);
            scheduleConfig = GsonUtil.fromJson(ScheduleConfig.class, configData);

            httpMap = new HashMap<String, String>();
            jobName = scheduleConfig.getJobName();
            groupName = scheduleConfig.getGroupName();
            uri = "http://" + scheduleConfig.getLoadId() + "/node/delete.htm";
            httpMap.put("jobName",jobName);
            httpMap.put("groupName",groupName);

            //使用http同步调用接口
            httpResult = HttpProtocol.http(uri,httpMap);
            String result = GsonUtil.getKeyValue("SUCCESS",GsonUtil.parseString(httpResult));
            if("true".equals(result)) {
                resultJson.setMsg("删除定时任务成功！");
                log.info("删除定时任务成功 id" + id);
            }else {
                resultJson.setMsg("删除定时任务失败！");
            }

        } catch (Exception e) {
            resultJson.setMsg("删除定时任务失败！");
            log.error("error in deleteTask", e.getMessage());
        }

        return resultJson;
    }


    @RequestMapping("/stopTask")
    @ResponseBody
    public ResultJson stopTask(@RequestParam(value = "id", required = false) String id) {
        ResultJson resultJson = new ResultJson();
        String taskPath = null;
        String configData = null;
        ScheduleConfig scheduleConfig = null;
        CuratorZKClient curatorZKClient = null;
        String uri = null;
        String jobName = null;
        String groupName = null;
        Map<String,String> httpMap = null;
        String httpResult = null;
        try {

            taskPath = ZKDictionary.INSTANCE.getTaskPath() + "/" + id;
            curatorZKClient = zookeeperMangeClient.getCuratorZKClient();
            configData = curatorZKClient.getData(taskPath);
            scheduleConfig = GsonUtil.fromJson(ScheduleConfig.class, configData);

            httpMap = new HashMap<String, String>();
            jobName = scheduleConfig.getJobName();
            groupName = scheduleConfig.getGroupName();
            uri = "http://" + scheduleConfig.getLoadId() + "/node/stop.htm";
            httpMap.put("jobName",jobName);
            httpMap.put("groupName",groupName);
            //使用http同步调用接口
            httpResult = HttpProtocol.http(uri,httpMap);
            String result = GsonUtil.getKeyValue("SUCCESS",GsonUtil.parseString(httpResult));
            if("true".equals(result)) {
                resultJson.setMsg("暂停定时任务成功！");
                log.info("暂停定时任务成功 id" + id);
            }else {
                resultJson.setMsg("暂停定时任务失败！");
            }
        } catch (Exception e) {
            resultJson.setMsg("暂停定时任务失败！");
            log.error("error in stopTask", e);
        }

        return resultJson;
    }


    @RequestMapping("/restartTask")
    @ResponseBody
    public ResultJson restartTask(@RequestParam(value = "id", required = false) String id) {
        ResultJson resultJson = new ResultJson();
        String taskPath = null;
        String configData = null;
        ScheduleConfig scheduleConfig = null;
        CuratorZKClient curatorZKClient = null;
        String uri = null;
        String jobName = null;
        String groupName = null;
        Map<String,String> httpMap = null;
        String httpResult = null;
        try {

            taskPath = ZKDictionary.INSTANCE.getTaskPath() + "/" + id;
            curatorZKClient = zookeeperMangeClient.getCuratorZKClient();
            configData = curatorZKClient.getData(taskPath);
            scheduleConfig = GsonUtil.fromJson(ScheduleConfig.class, configData);
            jobName = scheduleConfig.getJobName();
            groupName = scheduleConfig.getGroupName();

            httpMap = new HashMap<String, String>();
            uri = "http://" + scheduleConfig.getLoadId() + "/node/start.htm";
            httpMap.put("jobName",jobName);
            httpMap.put("groupName",groupName);
            httpResult = HttpProtocol.http(uri,httpMap);
            String result = GsonUtil.getKeyValue("SUCCESS",GsonUtil.parseString(httpResult));

            if("true".equals(result)) {
                resultJson.setMsg("重启定时任务成功！");
                log.info("重启定时任务成功 id"+id);
            }else{
                resultJson.setMsg("重启定时任务失败！");
            }

        } catch (Exception e) {
            resultJson.setMsg("重启定时任务失败！");
            log.error("error in stopTask", e);
        }

        return resultJson;
    }

    @RequestMapping("/modifyTask")
    @ResponseBody
    public ResultJson addTask(@RequestParam(value = "taskName", required = false) String taskName,
                              @RequestParam(value = "scheduleType", required = false) String scheduleType,
                              @RequestParam(value = "protocolType", required = false) String protocolType,
                              @RequestParam(value = "jobCron", required = false) String jobCron,
                              @RequestParam(value = "jobInterval",required = false) String jobInterval,
                              @RequestParam(value = "startTime", required = false) String startTime,
                              @RequestParam(value = "stopTime", required = false) String stopTime,
                              @RequestParam(value = "httpUrl", required = false) String httpUrl,
                              @RequestParam(value = "httpMethod", required = false) String httpMethod,
                              @RequestParam(value = "timeOut", required = false) String timeOut,
                              @RequestParam(value = "httpParams", required = false) String httpParams,
                              @RequestParam(value = "interfaceName", required = false) String interfaceName,
                              @RequestParam(value = "interfaceMethod", required = false) String interfaceMethod,
                              @RequestParam(value = "interfaceParamType", required = false,defaultValue = "String") String interfaceParamType,
                              @RequestParam(value = "interfaceVersion", required = false) String interfaceVersion,
                              @RequestParam(value = "interfaceAddress", required = false) String interfaceAddress,
                              @RequestParam(value = "interfaceParams", required = false) String interfaceParams,
                              @RequestParam(value = "interfaceGroup", required = false) String interfaceGroup,
                              @RequestParam(value = "id", required = false) String id) {

        ResultJson resultJson = new ResultJson();
        ScheduleConfig scheduleConfig = null;
        CuratorZKClient curatorZKClient = null;
        DubboConfig dubboConfig = null;
        HttpConfig httpConfig = null;
        String taskPath = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String userId = "123456789";
        try {
            scheduleConfig = new ScheduleConfig();
            scheduleConfig.setUserId(userId);
            scheduleConfig.setId(id);
            scheduleConfig.setProtocolType(protocolType);
            scheduleConfig.setTaskName(taskName);
            scheduleConfig.setScheduleType(scheduleType);
            if(ScheduleTypeEnum.CRON.name().equals(scheduleType)){
                scheduleConfig.setJobCron(jobCron);
            }else{
                scheduleConfig.setJobInterval(Long.valueOf(jobInterval));
            }
            if(!StringUtils.isBlank(startTime)) {
                scheduleConfig.setStartTime(format.parse(startTime));
            }
            if(!StringUtils.isBlank(stopTime)) {
                scheduleConfig.setStopTime(format.parse(stopTime));
            }
            if (ProtocolTypeEnum.DUBBO.name().equals(protocolType)) {
                dubboConfig = new DubboConfig();
                dubboConfig.setInterfaceGroup(interfaceGroup);
                dubboConfig.setInterfaceMethod(interfaceMethod);
                dubboConfig.setInterfaceName(interfaceName);
                dubboConfig.setInterfaceParams(interfaceParams);
                dubboConfig.setTimeOut(Integer.valueOf(timeOut));
                dubboConfig.setInterfaceParamType(interfaceParamType);
                dubboConfig.setInterfaceVersion(interfaceVersion);
                dubboConfig.setInterfaceAddress(interfaceAddress);
                scheduleConfig.setDubboConfig(dubboConfig);
            } else {
                httpConfig = new HttpConfig();
                httpConfig.setHttpUrl(httpUrl);
                httpConfig.setHttpMethod(httpMethod);
                httpConfig.setTimeOut(timeOut);
                if (HttpMethodEnum.POST.name().equals(httpMethod)) {
                    httpConfig.setHttpParams(httpParams);
                }
                scheduleConfig.setHttpConfig(httpConfig);
            }

            curatorZKClient = zookeeperMangeClient.getCuratorZKClient();
            taskPath = ZKDictionary.INSTANCE.getTaskPath();
            curatorZKClient.setData(taskPath +"/"+ id, GsonUtil.toJsonString(scheduleConfig));
            resultJson.setMsg("修改定时任务成功");

            log.info("修改定时任务成功 id"+id);
        } catch (Exception e) {
            resultJson.setMsg("修改定时任务失败");
            log.error("error in addTask", e.getMessage());
        }

        return resultJson;
    }


}
