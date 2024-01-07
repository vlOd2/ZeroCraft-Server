package org.zerocraft.server.net;

import java.io.IOException;

import org.zerocraft.server.ZeroCraftServer;
import org.zerocraft.server.logger.Logger;
import org.zerocraft.server.net.packet.Packet;
import org.zerocraft.server.net.packet.PacketLogout;
import org.zerocraft.server.net.packet.PacketPlayerTeleport;

public class NetBaseHandler {
	public static byte PROTOCOL_VERSION = 6;
	protected Logger logger = ZeroCraftServer.logger;
	public NetworkManager netManager;
	public String userName;
	public boolean connectionClosed;

	public void sendPacket(Packet packet) {
		if (!(packet instanceof PacketPlayerTeleport)) {			
			logger.verbose("Added %s to the send queue of %s", packet, this);
		}
		this.netManager.addToQueue(packet);
		this.netManager.interrupt();
	}

	public void kick(String reason) {
		if (this.connectionClosed) {
			return;
		}
		this.logger.info(String.format("Kicking %s: %s", this, reason));
		this.sendPacket(new PacketLogout(reason));
		this.netManager.close();
		this.connectionClosed = true;
		this.onDisconnect();
	}
	
	public void handleTermination(String reason) {
		this.logger.info(String.format("%s has disconnected: %s", this, reason));
		this.connectionClosed = true;
		this.onDisconnect();
	}

	public void handlePacket(Packet packet) {
		this.onBadPacket();
	}

	public void onUpdate() throws IOException {
	}

	protected void onBadPacket() {
		this.netManager.shutdown("Illegal packet during session type!");
	}

	protected void onDisconnect() {
	}
	
	@Override
	public String toString() {
		return this.userName != null ? String.format("%s [%s]", this.userName, this.netManager.getAddress())
				: this.netManager.getAddress().toString();
	}
}
