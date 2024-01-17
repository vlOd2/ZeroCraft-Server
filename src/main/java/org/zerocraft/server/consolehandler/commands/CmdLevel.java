package org.zerocraft.server.consolehandler.commands;

import org.zerocraft.server.ZeroCraftServer;
import org.zerocraft.server.consolehandler.ConsoleCaller;
import org.zerocraft.server.consolehandler.ConsoleCommand;

public class CmdLevel implements ConsoleCommand {
	@Override
	public String getName() {
		return "level";
	}

	@Override
	public String getDescription() {
		return "Teleports you to the specified level";
	}

	@Override
	public String getUsage() {
		return "level <level>";
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
		if (!caller.isClient) {
			caller.sendMessage("Only players may run this command!");
			return;
		}
		
		String level = args[0];
		
		switch (level) {
		case "main":
			caller.client.changeLevel(server.level);
			break;
		case "main2":
			caller.client.changeLevel(server.level2);
			break;
		default:
			caller.sendMessage("&8(&d!&8)&c Unable to find the level \"%s\"", level);
			break;
		}
	}
}
