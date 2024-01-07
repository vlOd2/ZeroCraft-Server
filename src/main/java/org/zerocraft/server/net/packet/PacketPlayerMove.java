package org.zerocraft.server.net.packet;

import java.io.IOException;

import org.zerocraft.server.net.ClassicDataInputStream;
import org.zerocraft.server.net.ClassicDataOutputStream;

public class PacketPlayerMove extends Packet {
	public byte id;
	public int xChangePos;
	public int yChangePos;
	public int zChangePos;

	public PacketPlayerMove() {
	}

	public PacketPlayerMove(byte id, int xPos, int yPos, int zPos) {
		this.id = id;
		this.xChangePos = xPos;
		this.yChangePos = yPos;
		this.zChangePos = zPos;
	}

	@Override
	public void read(ClassicDataInputStream stream) throws IOException {
		this.id = stream.readSByte();
		this.xChangePos = stream.read();
		this.yChangePos = stream.read();
		this.zChangePos = stream.read();
	}

	@Override
	public void write(ClassicDataOutputStream stream) throws IOException {
		stream.writeSByte(this.id);
		stream.write(this.xChangePos);
		stream.write(this.yChangePos);
		stream.write(this.zChangePos);
	}

	@Override
	public int getID() {
		return 10;
	}

	@Override
	public int getPacketSize() {
		return 1 + 1 + 1 + 1;
	}
}
