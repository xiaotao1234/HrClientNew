package com.huari.dataentry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ForSaveDeviceSetting implements Serializable {

    List<SimpleStation> stations = new ArrayList<>();

    public void setStations(List<SimpleStation> stations) {
        this.stations = stations;
    }

    public List<SimpleStation> getStations() {
        return stations;
    }

}
