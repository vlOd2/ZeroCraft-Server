package org.zerocraft.server.net;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.zerocraft.server.net.packet.Packet;

public interface NetworkManager {
	void setNetHandler(NetBaseHandler netHandler);

	void addToQueue(Packet packet);

	void shutdown(String reason);

	void processReceivedPackets() throws IOException;

	InetSocketAddress getSocketAddress();

	NetworkAddress getAddress();

	ClassicDataInputStream getInputStream();

	ClassicDataOutputStream getOutputStream();

	void interrupt();

	void close();
}
