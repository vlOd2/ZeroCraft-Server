package org.zerocraft.server.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByte extends NBTBase {
	public byte byteValue;

	public NBTTagByte() {
	}

	public NBTTagByte(byte value) {
		this.byteValue = value;
	}

	@Override
	public void writeContents(DataOutput dataOutput) throws IOException {
		dataOutput.writeByte(this.byteValue);
	}

	@Override
	public void readContents(DataInput dataInput) throws IOException {
		this.byteValue = dataInput.readByte();
	}

	@Override
	public byte getType() {
		return (byte) 1;
	}

	@Override
	public String toString() {
		return "" + this.byteValue;
	}
}
