package org.zerocraft.server.net.packet;

import java.io.IOException;

import org.zerocraft.server.net.ClassicDataInputStream;
import org.zerocraft.server.net.ClassicDataOutputStream;

public class PacketPlaceBreakTile extends Packet {
	public short xPos;
	public short yPos;
	public short zPos;
	public int mode;
	public int type;

	public PacketPlaceBreakTile() {
	}

	public PacketPlaceBreakTile(short xPos, short yPos, short zPos, int mode, int type) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;
		this.mode = mode;
		this.type = type;
	}

	@Override
	public void read(ClassicDataInputStream stream) throws IOException {
		this.xPos = stream.readShort();
		this.yPos = stream.readShort();
		this.zPos = stream.readShort();
		this.mode = stream.read();
		this.type = stream.read();
	}

	@Override
	public void write(ClassicDataOutputStream stream) throws IOException {
		stream.writeShort(this.xPos);
		stream.writeShort(this.yPos);
		stream.writeShort(this.zPos);
		stream.write(this.mode);
		stream.write(this.type);
	}

	@Override
	public int getID() {
		return 5;
	}

	@Override
	public int getPacketSize() {
		return 2 + 2 + 2 + 1 + 1;
	}
}
