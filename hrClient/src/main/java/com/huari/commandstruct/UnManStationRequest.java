package com.huari.commandstruct;

import struct.StructClass;
import struct.StructField;

@StructClass
public class UnManStationRequest {

	@StructField(order = 0)
	public byte[] head = new byte[] { (byte) 0xEE, (byte) 0xEE, (byte) 0xEE,
			(byte) 0xEE };
	@StructField(order = 1)
	public int length;
	@StructField(order = 2)
	public byte functionNum;
	@StructField(order = 3)
	public int framelength;
	@StructField(order = 4)
	public byte[] stationid;
	@StructField(order = 5)
	public byte onoroff;
	@StructField(order = 6)
	public byte[] switchname;
}
