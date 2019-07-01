package com.fengjr.cock.manage.web.utils.convert;

public class ResultJson {
    //返回的状态
    private int status;
    //消息
    private String msg;
    //返回的数据
    private Object data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
