package com.fengjr.cock.common.domain.http;

public class HttpConfig {

    private String httpUrl;
    private String httpContentType;
    private String timeOut;
    private String httpParams;
    private String httpMethod;
    private String HttpCharset;

    public String getHttpUrl() {
        return httpUrl == null ? "" : httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public String getHttpContentType() {
        return httpContentType == null ? "" : httpContentType;
    }

    public void setHttpContentType(String httpContentType) {
        this.httpContentType = httpContentType;
    }

    public String getTimeOut() {
        return timeOut == null ? "" : timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public String getHttpParams() {
        return httpParams == null ? "" : httpParams;
    }

    public void setHttpParams(String httpParams) {
        this.httpParams = httpParams;
    }

    public String getHttpMethod() {
        return httpMethod == null ? "" : httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getHttpCharset() {
        return HttpCharset == null ? "" : HttpCharset;
    }

    public void setHttpCharset(String httpCharset) {
        HttpCharset = httpCharset;
    }
}
