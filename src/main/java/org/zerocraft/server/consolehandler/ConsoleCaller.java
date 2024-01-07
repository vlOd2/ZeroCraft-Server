package org.zerocraft.server.consolehandler;

import org.zerocraft.server.ChatColor;
import org.zerocraft.server.ZeroCraftServer;
import org.zerocraft.server.net.NetServerHandler;

public class ConsoleCaller {
	public boolean isClient;
	public NetServerHandler client;
	
	public ConsoleCaller(NetServerHandler client) {
		this.isClient = client != null;
		this.client = client;
	}
	
	public void sendMessage(String format, Object... args) {
		String message = String.format(format, args);
		
		if (this.isClient) {
			this.client.sendChatMessage(message);
		} else {
			ZeroCraftServer.logger.info(ChatColor.stripColorCodes(message, '&'));
		}
	}
	
	public boolean hasPermission(String permission) {
		if (permission.equalsIgnoreCase("none")) {
			return true;
		}
		return this.client == null ? true : false;
	}
}
