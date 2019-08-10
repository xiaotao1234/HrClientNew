package com.huari.commandstruct;

import struct.StructClass;
import struct.StructField;

@StructClass
public class NoAntPPFXRequest {
	@StructField(order = 0)
	public byte[] head = new byte[] { (byte) 238, (byte) 238, (byte) 238,
			(byte) 238 };
	@StructField(order = 1)
	public int length;
	@StructField(order = 2)
	public byte functionNum;
	@StructField(order = 3)
	public byte[] stationid;
	@StructField(order = 4)
	public byte[] logicid;
	@StructField(order = 5)
	public byte[] devicename;
	@StructField(order = 6)
	public short pinduancount;
	@StructField(order = 7)
	public byte[] logictype;
	@StructField(order = 8)
	public int parameterslength;
	@StructField(order = 9)
	public PinPuParameter[] p;
}
