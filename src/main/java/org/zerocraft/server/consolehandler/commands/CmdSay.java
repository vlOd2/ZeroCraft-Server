package org.zerocraft.server.consolehandler.commands;

import org.zerocraft.server.ChatColor;
import org.zerocraft.server.ZeroCraftServer;
import org.zerocraft.server.consolehandler.ConsoleCaller;
import org.zerocraft.server.consolehandler.ConsoleCommand;

public class CmdSay implements ConsoleCommand {
	@Override
	public String getName() {
		return "say";
	}

	@Override
	public String getDescription() {
		return "Prints a message to the chat";
	}

	@Override
	public String getUsage() {
		return "say <message>";
	}

	@Override
	public int getMinArgsCount() {
		return 1;
	}

	@Override
	public int getMaxArgsCount() {
		return 1;
	}

	@Override
	public void execute(ZeroCraftServer server, ConsoleCaller caller, String[] args) throws Exception {
		String msg = args[0].trim();
		
		if (caller.isClient) {
			caller.client.sentMessage(msg);
		} else {
			ZeroCraftServer.logger.info("[CONSOLE] said: %s", msg);
			server.sendGlobalChatMessage(ChatColor.format("&8[&eCONSOLE&8]&f %s", msg));
		}
	}
}
