package org.zerocraft.server.net.packet;

import java.io.IOException;

import org.zerocraft.server.net.ClassicDataInputStream;
import org.zerocraft.server.net.ClassicDataOutputStream;

public class PacketPlayerTeleport extends Packet {
	public byte id;
	public short xPos;
	public short yPos;
	public short zPos;
	public int yaw;
	public int pitch;

	public PacketPlayerTeleport() {
	}

	public PacketPlayerTeleport(byte id, short xPos, short yPos, short zPos, int yaw, int pitch) {
		this.id = id;
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	@Override
	public void read(ClassicDataInputStream stream) throws IOException {
		this.id = stream.readSByte();
		this.xPos = stream.readShort();
		this.yPos = stream.readShort();
		this.zPos = stream.readShort();
		this.yaw = stream.read();
		this.pitch = stream.read();
	}

	@Override
	public void write(ClassicDataOutputStream stream) throws IOException {
		stream.writeSByte(this.id);
		stream.writeShort(this.xPos);
		stream.writeShort(this.yPos);
		stream.writeShort(this.zPos);
		stream.write(this.yaw);
		stream.write(this.pitch);
	}

	@Override
	public int getID() {
		return 8;
	}

	@Override
	public int getPacketSize() {
		return 1 + 2 + 2 + 2 + 1 + 1;
	}
}
