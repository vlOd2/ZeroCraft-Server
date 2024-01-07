package org.zerocraft.server.net.packet;

import java.io.IOException;

import org.zerocraft.server.net.ClassicDataInputStream;
import org.zerocraft.server.net.ClassicDataOutputStream;

public class PacketLogin extends Packet {
	public int protocolVersion;
	public String name;
	public String passwordHash;
	public int userType;

	public PacketLogin() {
	}

	public PacketLogin(byte protocolVersion, String name, String passwordHash, int userType) {
		this.protocolVersion = protocolVersion;
		this.name = name;
		this.passwordHash = passwordHash;
	}

	@Override
	public void read(ClassicDataInputStream stream) throws IOException {
		this.protocolVersion = stream.read();
		this.name = stream.readString();
		this.passwordHash = stream.readString();
		this.userType = stream.read();
	}

	@Override
	public void write(ClassicDataOutputStream stream) throws IOException {
		stream.write(this.protocolVersion);
		stream.writeString(this.name);
		stream.writeString(this.passwordHash);
		stream.write(this.userType);
	}

	@Override
	public int getID() {
		return 0;
	}

	@Override
	public int getPacketSize() {
		return 1 + 64 + 64 + 1;
	}
}
