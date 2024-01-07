package org.zerocraft.server.consolehandler.commands;

import org.zerocraft.server.ZeroCraftServer;
import org.zerocraft.server.consolehandler.ConsoleCaller;
import org.zerocraft.server.consolehandler.ConsoleCommand;
import org.zerocraft.server.net.NetServerHandler;

public class CmdKick implements ConsoleCommand {
	@Override
	public String getName() {
		return "kick";
	}

	@Override
	public String getDescription() {
		return "Kicks a person from the server";
	}

	@Override
	public String getUsage() {
		return "kick <player> [reason]";
	}

	@Override
	public int getMinArgsCount() {
		return 1;
	}

	@Override
	public int getMaxArgsCount() {
		return 2;
	}

	@Override
	public String getRequiredPermission() {
		return "cmd.kick";
	}
	
	@Override
	public void execute(ZeroCraftServer server, ConsoleCaller caller, String[] args) throws Exception {
		String player = args[0];
		String reason = args.length > 1 ? args[1] : "No reason specified";
		
		NetServerHandler netHandler = server.getHandlerByName(player);
		if (netHandler == null) {
			caller.sendMessage("&8(&d!&8)&c The player &e\"%s\"&c is not online", player);
			return;
		}
	
		netHandler.kick(reason);
		caller.sendMessage("&8(&d!&8)&a Kicked the user &e\"%s\"", player);
	}
}
