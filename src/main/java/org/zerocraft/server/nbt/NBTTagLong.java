package org.zerocraft.server.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagLong extends NBTBase {
	public long longValue;

	public NBTTagLong() {
	}

	public NBTTagLong(long value) {
		this.longValue = value;
	}

	@Override
	public void writeContents(DataOutput dataOutput) throws IOException {
		dataOutput.writeLong(this.longValue);
	}

	@Override
	public void readContents(DataInput dataInput) throws IOException {
		this.longValue = dataInput.readLong();
	}

	@Override
	public byte getType() {
		return (byte) 4;
	}

	@Override
	public String toString() {
		return "" + this.longValue;
	}
}
