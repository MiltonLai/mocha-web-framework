package com.rockbb.jshadow.web.base;

public class SessionBean {
    public static final String ATTR_KEY = "SESSION_BEAN";
    private long timestamp = System.currentTimeMillis();

    public long getTimestamp() {return timestamp;}
    public void setTimestamp(long timestamp) {this.timestamp = timestamp;}
}
