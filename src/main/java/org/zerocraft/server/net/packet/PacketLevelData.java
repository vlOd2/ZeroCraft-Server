package org.zerocraft.server.net.packet;

import java.io.IOException;

import org.zerocraft.server.net.ClassicDataInputStream;
import org.zerocraft.server.net.ClassicDataOutputStream;

public class PacketLevelData extends Packet {
	public short length;
	public byte[] data;
	public int progress;

	public PacketLevelData() {
	}

	public PacketLevelData(short length, byte[] data, int progress) {
		this.length = length;
		this.data = data;
		this.progress = progress;
	}

	@Override
	public void read(ClassicDataInputStream stream) throws IOException {
		this.length = stream.readShort();
		this.data = stream.readBytes();
		this.progress = stream.read();
	}

	@Override
	public void write(ClassicDataOutputStream stream) throws IOException {
		stream.writeShort(this.length);
		stream.writeBytes(this.data);
		stream.write(this.progress);
	}

	@Override
	public int getID() {
		return 3;
	}

	@Override
	public int getPacketSize() {
		return 2 + 1024 + 1;
	}
}
