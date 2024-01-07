package org.zerocraft.server.net.packet;

import java.io.IOException;

import org.zerocraft.server.net.ClassicDataInputStream;
import org.zerocraft.server.net.ClassicDataOutputStream;

public class PacketLogout extends Packet {
	public String reason;

	public PacketLogout() {
	}

	public PacketLogout(String reason) {
		this.reason = reason;
	}

	@Override
	public void read(ClassicDataInputStream stream) throws IOException {
		this.reason = stream.readString();
	}

	@Override
	public void write(ClassicDataOutputStream stream) throws IOException {
		stream.writeString(this.reason);
	}

	@Override
	public int getID() {
		return 14;
	}

	@Override
	public int getPacketSize() {
		return 64;
	}
}
