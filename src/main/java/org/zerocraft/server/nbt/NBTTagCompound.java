package org.zerocraft.server.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NBTTagCompound extends NBTBase {
	private Map<String, NBTBase> data = new HashMap<>();

	@Override
	public void writeContents(DataOutput dataOutput) throws IOException {
		for (NBTBase nbt : this.data.values()) {
			NBTBase.write(nbt, dataOutput);
		}

		dataOutput.writeByte(0);
	}

	@Override
	public void readContents(DataInput dataInput) throws IOException {
		this.data.clear();

		NBTBase nbt;
		while ((nbt = NBTBase.read(dataInput)).getType() != 0) {
			this.data.put(nbt.getKey(), nbt);
		}
	}

	public Collection<NBTBase> getData() {
		return this.data.values();
	}

	@Override
	public byte getType() {
		return (byte) 10;
	}

	public void setTag(String key, NBTBase nBTBase2) {
		this.data.put(key, nBTBase2.setKey(key));
	}

	public void setByte(String key, byte value) {
		this.data.put(key, new NBTTagByte(value).setKey(key));
	}

	public void setShort(String key, short value) {
		this.data.put(key, new NBTTagShort(value).setKey(key));
	}

	public void setInteger(String key, int value) {
		this.data.put(key, new NBTTagInt(value).setKey(key));
	}

	public void setLong(String key, long value) {
		this.data.put(key, new NBTTagLong(value).setKey(key));
	}

	public void setFloat(String key, float value) {
		this.data.put(key, new NBTTagFloat(value).setKey(key));
	}

	public void setDouble(String key, double value) {
		this.data.put(key, new NBTTagDouble(value).setKey(key));
	}

	public void setString(String key, String value) {
		this.data.put(key, new NBTTagString(value).setKey(key));
	}

	public void setByteArray(String key, byte[] value) {
		this.data.put(key, new NBTTagByteArray(value).setKey(key));
	}

	public void setCompoundTag(String key, NBTTagCompound value) {
		this.data.put(key, value.setKey(key));
	}

	public void setBoolean(String key, boolean value) {
		this.setByte(key, (byte) (value ? 1 : 0));
	}

	public boolean hasKey(String key) {
		return this.data.containsKey(key);
	}

	public byte getByte(String key) {
		return !this.data.containsKey(key) ? 0 : ((NBTTagByte) this.data.get(key)).byteValue;
	}

	public short getShort(String key) {
		return !this.data.containsKey(key) ? 0 : ((NBTTagShort) this.data.get(key)).shortValue;
	}

	public int getInteger(String key) {
		return !this.data.containsKey(key) ? 0 : ((NBTTagInt) this.data.get(key)).intValue;
	}

	public long getLong(String key) {
		return !this.data.containsKey(key) ? 0L : ((NBTTagLong) this.data.get(key)).longValue;
	}

	public float getFloat(String key) {
		return !this.data.containsKey(key) ? 0.0F : ((NBTTagFloat) this.data.get(key)).floatValue;
	}

	public double getDouble(String key) {
		return !this.data.containsKey(key) ? 0.0D : ((NBTTagDouble) this.data.get(key)).doubleValue;
	}

	public String getString(String key) {
		return !this.data.containsKey(key) ? "" : ((NBTTagString) this.data.get(key)).stringValue;
	}

	public byte[] getByteArray(String key) {
		return !this.data.containsKey(key) ? new byte[0] : ((NBTTagByteArray) this.data.get(key)).byteArray;
	}

	public NBTTagCompound getCompoundTag(String key) {
		return !this.data.containsKey(key) ? new NBTTagCompound() : (NBTTagCompound) this.data.get(key);
	}

	public NBTTagList getTagList(String key) {
		return !this.data.containsKey(key) ? new NBTTagList() : (NBTTagList) this.data.get(key);
	}

	public boolean getBoolean(String key) {
		return this.getByte(key) != 0;
	}

	@Override
	public String toString() {
		return "" + this.data.size() + " entries";
	}
}
