package com.huari.dataentry;

import java.io.File;
import java.io.Serializable;

public class ForADataInformation implements Serializable {

    String stationName;
    String deviceName;
    String logicId;
    MyDevice device;
    String fileName;

    public ForADataInformation(String stationName, String deviceName, String logicId, MyDevice device) {
        this.stationName = stationName;
        this.deviceName = deviceName;
        this.logicId = logicId;
        this.device = device;
    }

    public void setFile(String file) {
        this.fileName = file;
    }

    public String getFile() {
        return fileName;
    }

    public String getStationName() {
        return stationName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setLogicId(String logicId) {
        this.logicId = logicId;
    }

    public void setDevice(MyDevice device) {
        this.device = device;
    }

    public String getLogicId() {
        return logicId;
    }

    public MyDevice getDevice() {
        return device;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ForADataInformation){
            if(getFile().equals(((ForADataInformation) obj).fileName)){
                return true;
            }
        }
        return false;
    }
}
