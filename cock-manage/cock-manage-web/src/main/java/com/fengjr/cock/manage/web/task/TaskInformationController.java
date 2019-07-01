package com.fengjr.cock.manage.web.task;


import com.fengjr.cock.common.domain.schedule.ScheduleConfig;
import com.fengjr.cock.common.json.GsonUtil;
import com.fengjr.cock.common.zookeeper.CuratorZKClient;
import com.fengjr.cock.common.zookeeper.ZKDictionary;
import com.fengjr.cock.manage.service.zookeeper.ZookeeperMangeClient;
import com.fengjr.cock.manage.web.utils.convert.ResultJson;
import com.fengjr.upm.tools.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TaskInformationController {
    protected static final Logger log = LoggerFactory.getLogger(TaskInformationController.class);
    @Autowired
    private ZookeeperMangeClient zookeeperMangeClient;

    @RequestMapping("/getTaskInformations")
    public String getTaskInformations(Model model) {

        User currentUser = null;
        String userId = "123456789";
        String taskPath = null;
        CuratorZKClient curatorZKClient = null;
        List<ScheduleConfig> listConfig = null;
        List<String> nodeListPath = null;
        ScheduleConfig scheduleConfig = null;
        String configData = null;
        String nodePrx = null;

        try {

            taskPath = ZKDictionary.INSTANCE.getTaskPath();
            curatorZKClient = zookeeperMangeClient.getCuratorZKClient();
            nodeListPath = curatorZKClient.getChildren(taskPath);
            listConfig = new ArrayList<ScheduleConfig>();
            for (String nodePath : nodeListPath) {
                nodePrx = nodePath.substring(0, nodePath.indexOf("_"));
                if (userId.equals(nodePrx)) {
                    configData = curatorZKClient.getData(taskPath + "/" + nodePath);
                    scheduleConfig = GsonUtil.fromJson(ScheduleConfig.class, configData);
                    listConfig.add(scheduleConfig);
                }
            }

            model.addAttribute("listConfig", listConfig);
        } catch (Exception e) {
            log.error("error in getTaskInformations", e.getMessage());
        }

        return "taskviews/taskList";
    }


    @RequestMapping("/detailsTask")
    @ResponseBody
    public ResultJson getDetailsTask(@RequestParam(value = "id", required = false) String id) {
            ResultJson resultJson = new ResultJson();
            String taskPath = null;
            CuratorZKClient curatorZKClient = null;
            ScheduleConfig scheduleConfig = null;
            String configData = null;

            try {

                taskPath = ZKDictionary.INSTANCE.getTaskPath() + "/" + id;
                curatorZKClient = zookeeperMangeClient.getCuratorZKClient();
                configData = curatorZKClient.getData(taskPath);
                scheduleConfig = GsonUtil.fromJson(ScheduleConfig.class, configData);

            } catch (Exception e) {
               log.error("error in getDetailsTask",e.getMessage());
            }


        resultJson.setData(scheduleConfig);
        return resultJson;
    }


}
