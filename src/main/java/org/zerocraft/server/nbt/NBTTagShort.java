package org.zerocraft.server.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShort extends NBTBase {
	public short shortValue;

	public NBTTagShort() {
	}

	public NBTTagShort(short value) {
		this.shortValue = value;
	}

	@Override
	public void writeContents(DataOutput dataOutput) throws IOException {
		dataOutput.writeShort(this.shortValue);
	}

	@Override
	public void readContents(DataInput dataInput) throws IOException {
		this.shortValue = dataInput.readShort();
	}

	@Override
	public byte getType() {
		return (byte) 2;
	}

	@Override
	public String toString() {
		return "" + this.shortValue;
	}
}
