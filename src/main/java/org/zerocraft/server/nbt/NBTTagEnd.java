package org.zerocraft.server.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagEnd extends NBTBase {
	@Override
	public void readContents(DataInput dataInput) throws IOException {
	}

	@Override
	public void writeContents(DataOutput dataOutput) throws IOException {
	}

	@Override
	public byte getType() {
		return (byte) 0;
	}

	@Override
	public String toString() {
		return "END";
	}
}
