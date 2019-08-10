package com.huari.commandstruct;

import struct.StructClass;
import struct.StructField;

@StructClass
public class PinDuanScanFrame {

	@StructField(order = 0)
	public byte[] head = new byte[] { (byte) 238, (byte) 238, (byte) 238,
			(byte) 238 };
	@StructField(order = 1)
	public int length;
	@StructField(order = 2)
	public byte functionNum;
	@StructField(order = 3)
	public int ilength;
	@StructField(order = 4)
	public byte isend;
	@StructField(order = 5)
	public int count;
	@StructField(order = 6)
	public int startindex;
	@StructField(order = 7)
	public int endindex;
	@StructField(order = 8)
	public short[] data;
}
