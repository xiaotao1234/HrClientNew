package com.huari.dataentry;

import java.io.Serializable;
import java.util.HashMap;

public class MyDevice   implements Serializable {

	public String name;
	public byte logicParametersCount;
	public byte state;// 设备状态
	public byte isOccupied;
	public HashMap<String, LogicParameter> logic;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLogicParametersCount(byte logicParametersCount) {
		this.logicParametersCount = logicParametersCount;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public void setIsOccupied(byte isOccupied) {
		this.isOccupied = isOccupied;
	}

	public void setLogic(HashMap<String, LogicParameter> logic) {
		this.logic = logic;
	}

	public byte getLogicParametersCount() {

		return logicParametersCount;
	}

	public byte getState() {
		return state;
	}

	public byte getIsOccupied() {
		return isOccupied;
	}

	public HashMap<String, LogicParameter> getLogic() {
		return logic;
	}
}
