package com.huari.dataentry;

public class SocketStopEvent {
    public SocketStopEvent(boolean flag) {
        this.flag = flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    boolean flag;

    public boolean isFlag() {
        return flag;
    }
}
