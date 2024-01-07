package org.zerocraft.server.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagFloat extends NBTBase {
	public float floatValue;

	public NBTTagFloat() {
	}

	public NBTTagFloat(float value) {
		this.floatValue = value;
	}

	@Override
	public void writeContents(DataOutput dataOutput) throws IOException {
		dataOutput.writeFloat(this.floatValue);
	}

	@Override
	public void readContents(DataInput dataInput) throws IOException {
		this.floatValue = dataInput.readFloat();
	}

	@Override
	public byte getType() {
		return (byte) 5;
	}

	@Override
	public String toString() {
		return "" + this.floatValue;
	}
}
