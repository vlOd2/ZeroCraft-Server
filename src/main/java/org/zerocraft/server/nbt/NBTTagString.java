package org.zerocraft.server.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagString extends NBTBase {
	public String stringValue;

	public NBTTagString() {
	}

	public NBTTagString(String value) {
		this.stringValue = value;
		if (value == null) {
			throw new IllegalArgumentException("Empty string not allowed");
		}
	}

	@Override
	public void writeContents(DataOutput dataOutput) throws IOException {
		dataOutput.writeUTF(this.stringValue);
	}

	@Override
	public void readContents(DataInput dataInput) throws IOException {
		this.stringValue = dataInput.readUTF();
	}

	@Override
	public byte getType() {
		return (byte) 8;
	}

	@Override
	public String toString() {
		return "" + this.stringValue;
	}
}
