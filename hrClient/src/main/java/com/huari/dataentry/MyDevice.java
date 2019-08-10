package com.huari.dataentry;

import java.util.HashMap;

public class MyDevice {

	public String name;
	public byte logicParametersCount;
	public byte state;// 设备状态
	public byte isOccupied;
	public HashMap<String, LogicParameter> logic;
}
