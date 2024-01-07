package org.zerocraft.server.net.packet;

import java.io.IOException;

import org.zerocraft.server.net.ClassicDataInputStream;
import org.zerocraft.server.net.ClassicDataOutputStream;

public class PacketPlayerRotate extends Packet {
	public byte id;
	public int yaw;
	public int pitch;

	public PacketPlayerRotate() {
	}

	public PacketPlayerRotate(byte id, int yaw, int pitch) {
		this.id = id;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	@Override
	public void read(ClassicDataInputStream stream) throws IOException {
		this.id = stream.readSByte();
		this.yaw = stream.read();
		this.pitch = stream.read();
	}

	@Override
	public void write(ClassicDataOutputStream stream) throws IOException {
		stream.writeSByte(this.id);
		stream.write(this.yaw);
		stream.write(this.pitch);
	}

	@Override
	public int getID() {
		return 11;
	}

	@Override
	public int getPacketSize() {
		return 1 + 1 + 1;
	}
}
