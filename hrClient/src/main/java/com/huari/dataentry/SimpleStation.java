package com.huari.dataentry;


import java.io.Serializable;

public class SimpleStation implements Serializable {
    public String stationName;
    public String stationId;
    MyDevice device;

    public SimpleStation(String name, String id, MyDevice device) {
        this.stationName = name;
        this.stationId = id;
        this.device = device;
    }

    public String getName() {
        return stationName;
    }

    public String getId() {
        return stationId;
    }

    public MyDevice getDevice() {
        return device;
    }

    public void setName(String name) {
        this.stationName = name;
    }

    public void setId(String id) {
        this.stationId = id;
    }

    public void setDevice(MyDevice device) {
        this.device = device;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SimpleStation) {
            if(stationId.equals(((SimpleStation) obj).getId())){
                if (device.getName().equals(((SimpleStation) obj).getDevice().getName())){
                    return true;
                }
            }
//            return (stationId.equals(((SimpleStation) obj).getId()) && device.getName().equals(((SimpleStation) obj).getName()));
        }
        return false;
    }
}
