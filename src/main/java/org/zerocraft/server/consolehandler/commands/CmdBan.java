package org.zerocraft.server.consolehandler.commands;

import org.zerocraft.server.ZeroCraftServer;
import org.zerocraft.server.consolehandler.ConsoleCaller;
import org.zerocraft.server.consolehandler.ConsoleCommand;

public class CmdBan implements ConsoleCommand {
	@Override
	public String getName() {
		return "ban";
	}

	@Override
	public String getDescription() {
		return "Bans a person from the server";
	}

	@Override
	public String getUsage() {
		return "ban <player> [reason]";
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
		return "cmd.ban";
	}
	
	@Override
	public void execute(ZeroCraftServer server, ConsoleCaller caller, String[] args) throws Exception {
		String player = args[0];
		String reason = args.length > 1 ? args[1] : "The ban hammer has spoken!";
		server.banPlayer(player, reason, false);
		caller.sendMessage("&8(&d!&8)&a Banned the user &e\"%s\"", player);
	}
}
