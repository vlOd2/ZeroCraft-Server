package org.zerocraft.server.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class NBTBase {
	private String key;

	public String getKey() {
		return this.key == null ? "" : this.key;
	}

	public NBTBase setKey(String key) {
		this.key = key;
		return this;
	}

	public static NBTBase read(DataInput dataInput) throws IOException {
		byte id = dataInput.readByte();

		if (id == 0) {
			return new NBTTagEnd();
		}

		NBTBase nbt = create(id);
		nbt.key = dataInput.readUTF();
		nbt.readContents(dataInput);

		return nbt;
	}

	public static void write(NBTBase nbt, DataOutput dataOutput) throws IOException {
		dataOutput.writeByte(nbt.getType());

		if (nbt.getType() == 0) {
			return;
		}

		dataOutput.writeUTF(nbt.getKey());
		nbt.writeContents(dataOutput);
	}

	public abstract void writeContents(DataOutput dataOutput) throws IOException;

	public abstract void readContents(DataInput dataInput) throws IOException;

	public abstract byte getType();

	public static NBTBase create(byte id) {
		switch (id) {
		case 0:
			return new NBTTagEnd();
		case 1:
			return new NBTTagByte();
		case 2:
			return new NBTTagShort();
		case 3:
			return new NBTTagInt();
		case 4:
			return new NBTTagLong();
		case 5:
			return new NBTTagFloat();
		case 6:
			return new NBTTagDouble();
		case 7:
			return new NBTTagByteArray();
		case 8:
			return new NBTTagString();
		case 9:
			return new NBTTagList();
		case 10:
			return new NBTTagCompound();
		default:
			return null;
		}
	}

	public static String getName(byte id) {
		switch (id) {
		case 0:
			return "TAG_End";
		case 1:
			return "TAG_Byte";
		case 2:
			return "TAG_Short";
		case 3:
			return "TAG_Int";
		case 4:
			return "TAG_Long";
		case 5:
			return "TAG_Float";
		case 6:
			return "TAG_Double";
		case 7:
			return "TAG_Byte_Array";
		case 8:
			return "TAG_String";
		case 9:
			return "TAG_List";
		case 10:
			return "TAG_Compound";
		default:
			return "UNKNOWN";
		}
	}
}
