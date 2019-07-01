package com.fengjr.cock.manage.web.task;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TaskViewController {

    @RequestMapping("/taskAdd")
    private String testAdd(){
        return "taskviews/taskAdd";
    }

}
