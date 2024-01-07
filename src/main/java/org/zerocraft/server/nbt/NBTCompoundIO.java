package org.zerocraft.server.nbt;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NBTCompoundIO {
	public static NBTTagCompound read(InputStream inputStream) throws IOException {
		DataInputStream dataStream = new DataInputStream(new GZIPInputStream(inputStream));

		NBTTagCompound nbtCompound;
		try {
			nbtCompound = readCompound(dataStream);
		} finally {
			dataStream.close();
		}

		return nbtCompound;
	}

	public static void write(NBTTagCompound nbtCompound, OutputStream outputStream) throws IOException {
		DataOutputStream dataStream = new DataOutputStream(new GZIPOutputStream(outputStream));

		try {
			writeCompound(nbtCompound, dataStream);
		} finally {
			dataStream.close();
		}
	}

	private static NBTTagCompound readCompound(DataInput dataInput) throws IOException {
		NBTBase nbt = NBTBase.read(dataInput);

		if (nbt instanceof NBTTagCompound) {
			return (NBTTagCompound) nbt;
		} else {
			throw new IOException("Root tag must be a named compound tag");
		}
	}

	private static void writeCompound(NBTTagCompound nbtCompound, DataOutput dataOutput) throws IOException {
		NBTBase.write(nbtCompound, dataOutput);
	}
}
