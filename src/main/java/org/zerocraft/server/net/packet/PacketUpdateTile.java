package org.zerocraft.server.net.packet;

import java.io.IOException;

import org.zerocraft.server.net.ClassicDataInputStream;
import org.zerocraft.server.net.ClassicDataOutputStream;

public class PacketUpdateTile extends Packet {
	public short xPos;
	public short yPos;
	public short zPos;
	public int type;

	public PacketUpdateTile() {
	}

	public PacketUpdateTile(short xPos, short yPos, short zPos, byte type) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;
		this.type = type;
	}

	@Override
	public void read(ClassicDataInputStream stream) throws IOException {
		this.xPos = stream.readShort();
		this.yPos = stream.readShort();
		this.zPos = stream.readShort();
		this.type = stream.read();
	}

	@Override
	public void write(ClassicDataOutputStream stream) throws IOException {
		stream.writeShort(this.xPos);
		stream.writeShort(this.yPos);
		stream.writeShort(this.zPos);
		stream.write(this.type);
	}

	@Override
	public int getID() {
		return 6;
	}

	@Override
	public int getPacketSize() {
		return 2 + 2 + 2 + 1;
	}
}
