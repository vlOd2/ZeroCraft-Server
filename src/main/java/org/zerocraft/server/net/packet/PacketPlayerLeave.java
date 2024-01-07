package org.zerocraft.server.net.packet;

import java.io.IOException;

import org.zerocraft.server.net.ClassicDataInputStream;
import org.zerocraft.server.net.ClassicDataOutputStream;

public class PacketPlayerLeave extends Packet {
	public byte id;

	public PacketPlayerLeave() {
	}
	
	public PacketPlayerLeave(byte id) {
		this.id = id;
	}
	
	@Override
	public void read(ClassicDataInputStream stream) throws IOException {
		this.id = stream.readSByte();
	}

	@Override
	public void write(ClassicDataOutputStream stream) throws IOException {
		stream.writeSByte(this.id);
	}

	@Override
	public int getID() {
		return 12;
	}

	@Override
	public int getPacketSize() {
		return 1;
	}
}
