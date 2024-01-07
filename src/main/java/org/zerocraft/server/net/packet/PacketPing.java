package org.zerocraft.server.net.packet;

import java.io.IOException;

import org.zerocraft.server.net.ClassicDataInputStream;
import org.zerocraft.server.net.ClassicDataOutputStream;

public class PacketPing extends Packet {
	@Override
	public void read(ClassicDataInputStream stream) throws IOException {
	}

	@Override
	public void write(ClassicDataOutputStream stream) throws IOException {
	}

	@Override
	public int getID() {
		return 1;
	}

	@Override
	public int getPacketSize() {
		return 0;
	}
}
