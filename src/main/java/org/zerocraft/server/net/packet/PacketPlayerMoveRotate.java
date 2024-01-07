package org.zerocraft.server.net.packet;

import java.io.IOException;

import org.zerocraft.server.net.ClassicDataInputStream;
import org.zerocraft.server.net.ClassicDataOutputStream;

public class PacketPlayerMoveRotate extends Packet {
	public byte id;
	public int xChangePos;
	public int yChangePos;
	public int zChangePos;
	public int yaw;
	public int pitch;

	public PacketPlayerMoveRotate() {
	}

	public PacketPlayerMoveRotate(byte id, byte xPos, byte yPos, byte zPos, int yaw, int pitch) {
		this.id = id;
		this.xChangePos = xPos;
		this.yChangePos = yPos;
		this.zChangePos = zPos;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	@Override
	public void read(ClassicDataInputStream stream) throws IOException {
		this.id = stream.readSByte();
		this.xChangePos = stream.read();
		this.yChangePos = stream.read();
		this.zChangePos = stream.read();
		this.yaw = stream.read();
		this.pitch = stream.read();
	}

	@Override
	public void write(ClassicDataOutputStream stream) throws IOException {
		stream.writeSByte(this.id);
		stream.write(this.xChangePos);
		stream.write(this.yChangePos);
		stream.write(this.zChangePos);
		stream.write(this.yaw);
		stream.write(this.pitch);
	}

	@Override
	public int getID() {
		return 9;
	}

	@Override
	public int getPacketSize() {
		return 1 + 1 + 1 + 1 + 1 + 1;
	}
}
