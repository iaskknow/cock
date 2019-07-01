package com.fengjr.cock.manage.web.server;


import com.fengjr.cock.common.domain.schedule.ScheduleConfig;
import com.fengjr.cock.common.http.HttpProtocol;
import com.fengjr.cock.common.json.GsonUtil;
import com.fengjr.cock.common.zookeeper.CuratorZKClient;
import com.fengjr.cock.common.zookeeper.ZKDictionary;
import com.fengjr.cock.manage.domain.modules.Server.ServerInformation;
import com.fengjr.cock.manage.service.zookeeper.ZookeeperMangeClient;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ServerInformationController {

    protected static final Logger log = LoggerFactory.getLogger(ServerInformationController.class);

    @Autowired
    private ZookeeperMangeClient zookeeperMangeClient;

    @RequestMapping("/serverInformationList")
    public String getServerInformation(Model model) {

        String serverPath = null;
        List<String> serverList = null;
        CuratorZKClient curatorZKClient = null;
        Map<String,String> httpMap = null;
        String httpResult = null;
        List<ServerInformation> listServers = null;
        try {

            serverPath = ZKDictionary.INSTANCE.getServerPath();
            curatorZKClient = zookeeperMangeClient.getCuratorZKClient();
            serverList = curatorZKClient.getChildren(serverPath);
            listServers = new ArrayList<ServerInformation>();

            for (String ipPort: serverList) {

                ServerInformation serverInformation = new ServerInformation();
                String uriCount = "http://" + ipPort + "/node/taskCount.htm";
                String uriRunCount = "http://" + ipPort + "/node/runTaskCount.htm";

                //使用http同步调用接口
                httpResult = HttpProtocol.http(uriCount,httpMap);
                String resultCount = GsonUtil.getKeyValue("SUCCESS",GsonUtil.parseString(httpResult));
                String taskCount = GsonUtil.getKeyValue("COUNT",GsonUtil.parseString(httpResult));
                if ("true".equals(resultCount)){
                    serverInformation.setTotalTaskNums(taskCount);
                }

                httpResult = HttpProtocol.http(uriRunCount,httpMap);
                String resultRunCount = GsonUtil.getKeyValue("SUCCESS",GsonUtil.parseString(httpResult));
                String taskRunCount = GsonUtil.getKeyValue("COUNT",GsonUtil.parseString(httpResult));
                if ("true".equals(resultRunCount)){
                    serverInformation.setRuningTaskNums(taskRunCount);
                }
                serverInformation.setIpPort(ipPort);

                listServers.add(serverInformation);
            }

            model.addAttribute("listServers",listServers);

        } catch (Exception e) {
            log.error("error in getServerInformation", e.getMessage());
        }

        return "taskviews/serverInformation";
    }

    @RequestMapping("/tasksInServer")
    public String getTasksInServer(@RequestParam(value = "ipPort",required = false) String ipPort,
                                 Model model){

        String taskPath = null;
        CuratorZKClient curatorZKClient = null;
        List<ScheduleConfig> listConfig = null;
        List<String> nodeListPath = null;
        ScheduleConfig scheduleConfig = null;
        String configData = null;
        String loadId = null;

        try {

            taskPath = ZKDictionary.INSTANCE.getTaskPath();
            curatorZKClient = zookeeperMangeClient.getCuratorZKClient();
            nodeListPath = curatorZKClient.getChildren(taskPath);
            listConfig = new ArrayList<ScheduleConfig>();
            for (String nodePath : nodeListPath) {
                configData = curatorZKClient.getData(taskPath + "/" + nodePath);
                scheduleConfig = GsonUtil.fromJson(ScheduleConfig.class, configData);
                loadId = scheduleConfig.getLoadId();
                if (!StringUtils.isEmpty(loadId) && loadId.equals(ipPort)) {
                    listConfig.add(scheduleConfig);
                }
            }

            model.addAttribute("listConfig", listConfig);
        } catch (Exception e) {
            log.error("error in getTasksInServer", e.getMessage());
        }

        return "taskviews/taskInServer";

    }


}
