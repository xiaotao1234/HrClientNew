package com.huari.dataentry;


import java.io.Serializable;
import java.util.ArrayList;

public class Station implements Serializable {

	public byte isCenter;
	public String centerName;
	public String id;
	public String parentId;
	public String name;
	public float lon;
	public float lan;
	public ArrayList<MyDevice> devicelist;
	public ArrayList<MyDevice> showdevicelist;

	public byte getIsCenter() {
		return isCenter;
	}

	public void setIsCenter(byte isCenter) {
		this.isCenter = isCenter;
	}

	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLon(float lon) {
		this.lon = lon;
	}

	public void setLan(float lan) {
		this.lan = lan;
	}

	public void setDevicelist(ArrayList<MyDevice> devicelist) {
		this.devicelist = devicelist;
	}

	public void setShowdevicelist(ArrayList<MyDevice> showdevicelist) {
		this.showdevicelist = showdevicelist;
	}

	public String getCenterName() {
		return centerName;
	}

	public String getId() {
		return id;
	}

	public String getParentId() {
		return parentId;
	}

	public String getName() {
		return name;
	}

	public float getLon() {
		return lon;
	}

	public float getLan() {
		return lan;
	}

	public ArrayList<MyDevice> getDevicelist() {
		return devicelist;
	}

	public ArrayList<MyDevice> getShowdevicelist() {
		return showdevicelist;
	}
}
