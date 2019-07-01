package com.fengjr.cock.manage.web.task;

import com.fengjr.cock.common.domain.dubbo.DubboConfig;
import com.fengjr.cock.common.domain.enums.HttpMethodEnum;
import com.fengjr.cock.common.domain.enums.ProtocolTypeEnum;
import com.fengjr.cock.common.domain.enums.ScheduleTypeEnum;
import com.fengjr.cock.common.domain.http.HttpConfig;
import com.fengjr.cock.common.domain.schedule.ScheduleConfig;
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

@Controller
@RequestMapping("/cock")
public class TaskAddController {
    protected static final Logger log = LoggerFactory.getLogger(TaskAddController.class);
    @Autowired
    private ZookeeperMangeClient zookeeperMangeClient;

    @RequestMapping("/addTask")
    @ResponseBody
    public ResultJson addTask(@RequestParam(value = "taskName",required = false) String taskName,
                              @RequestParam(value = "scheduleType",required = false) String scheduleType,
                              @RequestParam(value = "protocolType",required = false) String protocolType,
                              @RequestParam(value = "jobCron",required = false) String jobCron,
                              @RequestParam(value = "jobInterval",required = false) String jobInterval,
                              @RequestParam(value = "startTime",required = false) String startTime,
                              @RequestParam(value = "stopTime",required = false) String stopTime,
                              @RequestParam(value = "httpUrl",required = false) String httpUrl,
                              @RequestParam(value = "httpMethod",required = false) String httpMethod,
                              @RequestParam(value = "timeOut",required = false) String timeOut,
                              @RequestParam(value = "httpParams",required = false) String httpParams,
                              @RequestParam(value = "interfaceName",required = false) String interfaceName,
                              @RequestParam(value = "interfaceMethod",required = false) String interfaceMethod,
                              @RequestParam(value = "interfaceParamType",required = false,defaultValue = "String") String interfaceParamType,
                              @RequestParam(value = "interfaceVersion",required = false) String interfaceVersion,
                              @RequestParam(value = "interfaceAddress",required = false) String interfaceAddress,
                              @RequestParam(value = "interfaceParams",required = false) String interfaceParams,
                              @RequestParam(value = "interfaceGroup",required = false) String interfaceGroup){

        ResultJson resultJson = new ResultJson();
        ScheduleConfig scheduleConfig = null;
        CuratorZKClient curatorZKClient = null;
        DubboConfig dubboConfig = null;
        HttpConfig httpConfig = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String userId = "123456789";
        String id = null;
        String taskPath = null;
        String path = null;

        try {
            scheduleConfig = new ScheduleConfig();
            id = userId + "_";
            scheduleConfig.setUserId(userId);
            scheduleConfig.setProtocolType(protocolType);
            scheduleConfig.setTaskName(taskName);
            scheduleConfig.setScheduleType(scheduleType);
            if(ScheduleTypeEnum.CRON.name().equals(scheduleType)){
                scheduleConfig.setJobCron(jobCron);
            }else{
                scheduleConfig.setJobInterval(Long.valueOf(jobInterval));
            }
            if(!StringUtils.isEmpty(startTime)) {
                scheduleConfig.setStartTime(format.parse(startTime));
            }
            if(!StringUtils.isEmpty(stopTime)) {
                scheduleConfig.setStopTime(format.parse(stopTime));
            }
            if(ProtocolTypeEnum.DUBBO.name().equals(protocolType)){
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
            }
            else{
                httpConfig = new HttpConfig();
                httpConfig.setHttpUrl(httpUrl);
                httpConfig.setHttpMethod(httpMethod);
                httpConfig.setTimeOut(timeOut);
                if(HttpMethodEnum.POST.name().equals(httpMethod)){
                    httpConfig.setHttpParams(httpParams);
                }
                scheduleConfig.setHttpConfig(httpConfig);
            }

            curatorZKClient = zookeeperMangeClient.getCuratorZKClient();
            taskPath = ZKDictionary.INSTANCE.getTaskPath();
            path = curatorZKClient.createPersistentSequential(taskPath+"/"+id);
            scheduleConfig.setId(path.replace("/fengjr/cock/task/",""));
            curatorZKClient.setData(path, GsonUtil.toJsonString(scheduleConfig));

            resultJson.setMsg("定时任务创建成功");
        } catch (Exception e) {
            resultJson.setMsg("定时任务创建失败");
            log.error("error in addTask",e.getMessage());
        }

        return resultJson;
    }
}
