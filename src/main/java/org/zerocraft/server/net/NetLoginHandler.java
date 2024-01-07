package org.zerocraft.server.net;

import java.io.IOException;

import org.zerocraft.server.ZeroCraftServer;
import org.zerocraft.server.configuration.MainConfig;
import org.zerocraft.server.net.packet.Packet;
import org.zerocraft.server.net.packet.PacketLogin;

public class NetLoginHandler extends NetBaseHandler {
	private ZeroCraftServer instance;
	private int notLoggedInTimer;
	private boolean doingLogin;

	public NetLoginHandler(ZeroCraftServer instance, NetworkManager netManager, String threadName) throws IOException {
		this.instance = instance;
		this.netManager = netManager;
		this.netManager.setNetHandler(this);
		this.logger.info(String.format("%s has connected", this.netManager.getAddress()));
	}

    @Override
	public void onUpdate() throws IOException {
		if (this.notLoggedInTimer++ == 100) { // 5 seconds
			this.kick("Took too long to log in");
		} else {
			this.netManager.processReceivedPackets();
			this.netManager.interrupt();
		}
	}

	@Override
	public void handlePacket(Packet packet) {
		logger.verbose("Received %s from %s", packet, this);
		
		if (packet instanceof PacketLogin) {
			this.handleLoginPacket((PacketLogin)packet);
		} else {
			super.handlePacket(packet);
		}
	}
	
	public void handleLoginPacket(PacketLogin packet) {
		if (this.doingLogin) {
			return;
		}
		this.doingLogin = true;
		
		if (!NetUtils.performModerationChecks(this, packet.name) || 
			!NetUtils.performNameVerification(this, packet.name)) {
			return;
		}
		
		if (packet.protocolVersion != PROTOCOL_VERSION) {
			this.kick("Mismatched protocol version!");
			return;
		}
		
    	if (this.instance.clients.size() + 1 > MainConfig.instance.maxUsers) {
    		this.kick("The server is full!");
    		return;
    	}
		
		this.userName = packet.name;
    	if (this.instance.getHandlerByName(this.userName) != null) {
    		this.kick("Someone with this username is already connected!");
    		return;
    	}
    	
    	NetServerHandler netHandler = new NetServerHandler(this.instance, this.netManager, this.userName);
    	this.instance.networkServer.addServerHandler(netHandler);
		this.connectionClosed = true;
	}
}
