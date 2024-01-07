package org.zerocraft.server.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagInt extends NBTBase {
	public int intValue;

	public NBTTagInt() {
	}

	public NBTTagInt(int value) {
		this.intValue = value;
	}

	@Override
	public void writeContents(DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(this.intValue);
	}

	@Override
	public void readContents(DataInput dataInput) throws IOException {
		this.intValue = dataInput.readInt();
	}

	@Override
	public byte getType() {
		return (byte) 3;
	}

	@Override
	public String toString() {
		return "" + this.intValue;
	}
}
