package com.fengjr.cock.common.domain.dubbo;

public class DubboConfig {

    private String interfaceName;
    private String interfaceMethod;
    private String interfaceParamType;
    private String interfaceVersion;
    private String interfaceGroup;
    private String interfaceAddress;
    private int timeOut;
    private String interfaceParams;

    public String getInterfaceName() {
        return interfaceName==null?"":interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getInterfaceMethod() {
        return interfaceMethod==null?"":interfaceMethod;
    }

    public void setInterfaceMethod(String interfaceMethod) {
        this.interfaceMethod = interfaceMethod;
    }

    public String getInterfaceParamType() {
        return interfaceParamType==null?"":interfaceParamType;
    }

    public void setInterfaceParamType(String interfaceParamType) {
        this.interfaceParamType = interfaceParamType;
    }

    public String getInterfaceVersion() {
        return interfaceVersion==null?"":interfaceVersion;
    }

    public void setInterfaceVersion(String interfaceVersion) {
        this.interfaceVersion = interfaceVersion;
    }

    public String getInterfaceGroup() {
        return interfaceGroup==null?"":interfaceGroup;
    }

    public void setInterfaceGroup(String interfaceGroup) {
        this.interfaceGroup = interfaceGroup;
    }

    public String getInterfaceAddress() {
        return interfaceAddress==null?"":interfaceAddress;
    }

    public void setInterfaceAddress(String interfaceAddress) {
        this.interfaceAddress = interfaceAddress;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public String getInterfaceParams() {
        return interfaceParams==null?"":interfaceParams;
    }

    public void setInterfaceParams(String interfaceParams) {
        this.interfaceParams = interfaceParams;
    }
}
