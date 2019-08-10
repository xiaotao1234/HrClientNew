package com.huari.commandstruct;

import struct.StructClass;
import struct.StructField;

@StructClass
public class FunctionFrame {

	@StructField(order = 0)
	public byte[] head = new byte[] { (byte) 0xee, (byte) 0xee, (byte) 0xee,
			(byte) 0xee };
	@StructField(order = 1)
	public int length;
	@StructField(order = 2)
	public byte functionNum;

}
