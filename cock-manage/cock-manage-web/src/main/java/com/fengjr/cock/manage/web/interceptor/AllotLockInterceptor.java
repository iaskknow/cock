package com.fengjr.cock.manage.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.fengjr.cock.common.domain.schedule.AllotDomain;
import com.fengjr.cock.common.json.GsonUtil;
import com.fengjr.cock.common.zookeeper.CuratorZKClient;
import com.fengjr.cock.common.zookeeper.ZKDictionary;
import com.fengjr.cock.manage.service.zookeeper.ZookeeperMangeClient;
import com.fengjr.cock.manage.web.task.TaskOperatorController;
import com.fengjr.cock.manage.web.utils.convert.ResultJson;

@Component
public class AllotLockInterceptor implements HandlerInterceptor {
    protected static final Logger log = LoggerFactory.getLogger(TaskOperatorController.class);

    @Autowired
    private ZookeeperMangeClient zookeeperMangeClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String allotLockPath = ZKDictionary.INSTANCE.getAllottingLockPath();
        CuratorZKClient curatorZKClient = zookeeperMangeClient.getCuratorZKClient();
        ResultJson resultJson = new ResultJson();
        boolean allot = true;
        boolean isExists = curatorZKClient.checkExists(allotLockPath);

        if (isExists) {
            String data = curatorZKClient.getData(allotLockPath);
            AllotDomain domain = GsonUtil.fromJson(AllotDomain.class, data);
            if("0".equals(domain.getStatus())){
                allot = false;
                response.setCharacterEncoding("utf-8");
                resultJson.setMsg("任务正在分配中，请稍后执行相关操作");
                response.getWriter().write(GsonUtil.toJsonString(resultJson));
            }
        }

        return allot;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

	
}
