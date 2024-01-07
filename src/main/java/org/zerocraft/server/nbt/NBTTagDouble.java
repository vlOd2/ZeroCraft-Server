package org.zerocraft.server.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagDouble extends NBTBase {
	public double doubleValue;

	public NBTTagDouble() {
	}

	public NBTTagDouble(double value) {
		this.doubleValue = value;
	}

	@Override
	public void writeContents(DataOutput dataOutput) throws IOException {
		dataOutput.writeDouble(this.doubleValue);
	}

	@Override
	public void readContents(DataInput dataInput) throws IOException {
		this.doubleValue = dataInput.readDouble();
	}

	@Override
	public byte getType() {
		return (byte) 6;
	}

	@Override
	public String toString() {
		return "" + this.doubleValue;
	}
}
