package org.zerocraft.server.net.packet;

import java.io.IOException;

import org.zerocraft.server.net.ClassicDataInputStream;
import org.zerocraft.server.net.ClassicDataOutputStream;

public class PacketLevelEnd extends Packet {
	public short xSize;
	public short ySize;
	public short zSize;

	public PacketLevelEnd() {
	}
	
	public PacketLevelEnd(short xSize, short ySize, short zSize) {
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;
	}
	
	@Override
	public void read(ClassicDataInputStream stream) throws IOException {
		this.xSize = stream.readShort();
		this.ySize = stream.readShort();
		this.zSize = stream.readShort();
	}

	@Override
	public void write(ClassicDataOutputStream stream) throws IOException {
		stream.writeShort(this.xSize);
		stream.writeShort(this.ySize);
		stream.writeShort(this.zSize);
	}

	@Override
	public int getID() {
		return 4;
	}

	@Override
	public int getPacketSize() {
		return 2 + 2 + 2;
	}
}
