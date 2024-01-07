package org.zerocraft.server.consolehandler.commands;

import org.zerocraft.server.ZeroCraftServer;
import org.zerocraft.server.consolehandler.ConsoleCaller;
import org.zerocraft.server.consolehandler.ConsoleCommand;

public class CmdUnban implements ConsoleCommand {
	@Override
	public String getName() {
		return "unban";
	}

	@Override
	public String getDescription() {
		return "Un-bans a person from the server";
	}

	@Override
	public String getUsage() {
		return "unban <player>";
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
	public String getRequiredPermission() {
		return "cmd.unban";
	}
	
	@Override
	public void execute(ZeroCraftServer server, ConsoleCaller caller, String[] args) throws Exception {
		String player = args[0];
		server.unbanPlayer(player, false);
		caller.sendMessage("&8(&d!&8)&a Un-banned the user &e\"%s\"", player);
	}
}
