package com.huari.dataentry;

import java.io.Serializable;
import java.util.ArrayList;

public class LogicParameter implements Serializable {

	public String id;
	public String type;
	public String name;
	public ArrayList<Parameter> parameterlist;

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParameterlist(ArrayList<Parameter> parameterlist) {
		this.parameterlist = parameterlist;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Parameter> getParameterlist() {
		return parameterlist;
	}
}
