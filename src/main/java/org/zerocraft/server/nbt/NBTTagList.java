package org.zerocraft.server.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class NBTTagList extends NBTBase implements List<NBTBase> {
	private List<NBTBase> data = new ArrayList<>();
	private byte tagType;

	@Override
	public void writeContents(DataOutput dataOutput) throws IOException {
		if (this.data.size() > 0) {
			this.tagType = this.data.get(0).getType();
		} else {
			this.tagType = 1;
		}

		dataOutput.writeByte(this.tagType);
		dataOutput.writeInt(this.data.size());

		for (NBTBase nbt : this.data) {
			nbt.writeContents(dataOutput);
		}
	}

	@Override
	public void readContents(DataInput dataInput) throws IOException {
		this.tagType = dataInput.readByte();
		this.data = new ArrayList<>();
		int itemsCount = dataInput.readInt();

		for (int i = 0; i < itemsCount; i++) {
			NBTBase nbt = NBTBase.create(this.tagType);
			nbt.readContents(dataInput);
			this.data.add(nbt);
		}
	}

	@Override
	public byte getType() {
		return (byte) 9;
	}

	@Override
	public String toString() {
		return "" + this.data.size() + " entries of type " + NBTBase.getName(this.tagType);
	}

	@Override
	public int size() {
		return this.data.size();
	}

	@Override
	public boolean isEmpty() {
		return this.data.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return this.data.contains(o);
	}

	@Override
	public Iterator<NBTBase> iterator() {
		return this.data.iterator();
	}

	@Override
	public Object[] toArray() {
		return this.data.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.data.toArray(a);
	}

	@Override
	public boolean add(NBTBase e) {
		return this.data.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return this.data.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.data.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends NBTBase> c) {
		return this.data.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends NBTBase> c) {
		return this.data.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.data.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.data.retainAll(c);
	}

	@Override
	public void clear() {
		this.data.clear();
	}

	@Override
	public NBTBase get(int index) {
		return this.data.get(index);
	}

	@Override
	public NBTBase set(int index, NBTBase element) {
		return this.data.set(index, element);
	}

	@Override
	public void add(int index, NBTBase element) {
		this.data.add(index, element);
	}

	@Override
	public NBTBase remove(int index) {
		return this.data.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return this.data.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return this.data.lastIndexOf(o);
	}

	@Override
	public ListIterator<NBTBase> listIterator() {
		return this.data.listIterator();
	}

	@Override
	public ListIterator<NBTBase> listIterator(int index) {
		return this.data.listIterator(index);
	}

	@Override
	public List<NBTBase> subList(int fromIndex, int toIndex) {
		return this.data.subList(fromIndex, toIndex);
	}
}
