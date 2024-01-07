package org.zerocraft.server.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByteArray extends NBTBase {
	public byte[] byteArray;

	public NBTTagByteArray() {
	}

	public NBTTagByteArray(byte[] value) {
		this.byteArray = value;
	}

	@Override
	public void writeContents(DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(this.byteArray.length);
		dataOutput.write(this.byteArray);
	}

	@Override
	public void readContents(DataInput dataInput) throws IOException {
		int i2 = dataInput.readInt();
		this.byteArray = new byte[i2];
		dataInput.readFully(this.byteArray);
	}

	@Override
	public byte getType() {
		return (byte) 7;
	}

	@Override
	public String toString() {
		return "[" + this.byteArray.length + " bytes]";
	}
}
