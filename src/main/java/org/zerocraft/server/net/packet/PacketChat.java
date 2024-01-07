package org.zerocraft.server.net.packet;

import java.io.IOException;

import org.zerocraft.server.net.ClassicDataInputStream;
import org.zerocraft.server.net.ClassicDataOutputStream;

public class PacketChat extends Packet {
	public byte player;
	public String message;

	public PacketChat() {
	}

	public PacketChat(byte player, String message) {
		this.player = player;
		this.message = message;
	}

	@Override
	public void read(ClassicDataInputStream stream) throws IOException {
		this.player = stream.readSByte();
		this.message = stream.readString();
	}

	@Override
	public void write(ClassicDataOutputStream stream) throws IOException {
		stream.writeSByte(this.player);
		stream.writeString(this.message);
	}

	@Override
	public int getID() {
		return 13;
	}

	@Override
	public int getPacketSize() {
		return 1 + 64;
	}
}
