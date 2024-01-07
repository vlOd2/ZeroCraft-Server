package org.zerocraft.server.net.packet;

import java.io.IOException;

import org.zerocraft.server.net.ClassicDataInputStream;
import org.zerocraft.server.net.ClassicDataOutputStream;

public abstract class Packet {
	public abstract void read(ClassicDataInputStream stream) throws IOException;

	public abstract void write(ClassicDataOutputStream stream) throws IOException;

	public abstract int getID();

	public abstract int getPacketSize();

	@Override
	public String toString() {
		return String.format("%s (%d)", this.getClass().getSimpleName(), this.getID());
	}
}