package org.zerocraft.server.consolehandler.commands;

import org.zerocraft.server.ZeroCraftServer;
import org.zerocraft.server.consolehandler.ConsoleCaller;
import org.zerocraft.server.consolehandler.ConsoleCommand;

public class CmdStop implements ConsoleCommand {
	@Override
	public String getName() {
		return "stop";
	}

	@Override
	public String getDescription() {
		return "Stops the server";
	}

	@Override
	public String getUsage() {
		return "stop";
	}

	@Override
	public int getMinArgsCount() {
		return 0;
	}

	@Override
	public int getMaxArgsCount() {
		return 0;
	}

	@Override
	public String getRequiredPermission() {
		return "cmd.stop";
	}
	
	@Override
	public void execute(ZeroCraftServer server, ConsoleCaller caller, String[] args) throws Exception {
		server.stop();
	}
}
