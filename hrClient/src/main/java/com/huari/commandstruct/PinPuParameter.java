package com.huari.commandstruct;

import struct.StructClass;
import struct.StructField;

@StructClass
public class PinPuParameter {

	@StructField(order = 0)
	public byte[] name;
	@StructField(order = 1)
	public byte[] value;
}
