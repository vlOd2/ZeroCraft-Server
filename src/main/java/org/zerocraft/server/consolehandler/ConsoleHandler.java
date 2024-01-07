package org.zerocraft.server.consolehandler;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.zerocraft.server.Tuple;
import org.zerocraft.server.Utils;
import org.zerocraft.server.ZeroCraftServer;
import org.zerocraft.server.consolehandler.commands.CmdBan;
import org.zerocraft.server.consolehandler.commands.CmdKick;
import org.zerocraft.server.consolehandler.commands.CmdSay;
import org.zerocraft.server.consolehandler.commands.CmdUnban;

public class ConsoleHandler {
	protected ZeroCraftServer server;
	public final ArrayList<ConsoleCommand> commands = new ArrayList<ConsoleCommand>();

	public ConsoleHandler(ZeroCraftServer server) {
		this.server = server;
		this.commands.add(new CmdSay());
		this.commands.add(new CmdBan());
		this.commands.add(new CmdUnban());
		this.commands.add(new CmdKick());
	}

	public void handleInput(String input, ConsoleCaller caller) {
		if (input == null || input.isEmpty()) {			
			return;
		}
		Tuple<String, String[]> inputParsed = this.parseInput(input.trim());

		if (inputParsed.item1.equalsIgnoreCase("help")) {
			if (inputParsed.item2.length > 0) {
				String helpArg = inputParsed.item2[0];
				
				if (Utils.isNumeric(helpArg, false)) {
					this.doHelpCommand(Integer.valueOf(helpArg), null, caller);
				} else {
					this.doHelpCommand(0, helpArg, caller);
				}
			} else {
				this.doHelpCommand(0, null, caller);
			}
		} else {
			this.processCommand(inputParsed.item1, inputParsed.item2, caller);
		}
	}

	protected Tuple<String, String[]> parseInput(String input) {
		String cmd = null;
		String[] cmdArgs = new String[0];

		if (input.contains(" ")) {
			String[] splittedInput = Utils.splitBySpace(input);
			cmd = splittedInput[0];
			cmdArgs = (String[]) ArrayUtils.subarray(splittedInput, 1, splittedInput.length);

			for (int cmdArgIndex = 0; cmdArgIndex < cmdArgs.length; cmdArgIndex++) {
				String cmdArg = cmdArgs[cmdArgIndex];

				if (cmdArg.startsWith("\"")) {
					cmdArg = cmdArg.substring(1, cmdArg.length() - 1);
				}

				if (cmdArg.endsWith("\"")) {
					cmdArg = cmdArg.substring(0, cmdArg.length() - 1);
				}

				cmdArgs[cmdArgIndex] = cmdArg;
			}
		} else {
			cmd = input;
		}

		return new Tuple<String, String[]>(cmd, cmdArgs);
	}

	public boolean processCommand(String cmd, String[] cmdArgs, ConsoleCaller caller) {
		ConsoleCommand command = getCommandByName(cmd);

		if (command != null) {
			if (!(cmdArgs.length < command.getMinArgsCount() || cmdArgs.length > command.getMaxArgsCount())) {
				if (!caller.hasPermission(command.getRequiredPermission())) {
					caller.sendMessage("&8(&d!&8)&c Insufficient permissions");
					return true;
				}

				try {
					command.execute(this.server, caller, cmdArgs);
				} catch (Exception ex) {
					caller.sendMessage("&8(&d!&8)&c An internal error has occured");
					ZeroCraftServer.logger.error("Unable to execute \"%s\": %s", cmd, 
							Utils.getThrowableStackTraceAsStr(ex));
				}
			} else {
				caller.sendMessage("&8(&d!&8)&a Usage&7:&e %s", command.getUsage());
			}
			return true;
		}

		caller.sendMessage("&8(&d!&8)&c Unrecognized command \"%s\"!", cmd);
		return false;
	}

	public ConsoleCommand getCommandByName(String name) {
		for (ConsoleCommand conCmd : this.commands) {
			if (conCmd.getName().equalsIgnoreCase(name)
					|| Arrays.asList(conCmd.getAliases()).stream().anyMatch(x -> x.equalsIgnoreCase(name))) {
				return conCmd;
			}
		}

		return null;
	}

	public void printConCmdHelp(ConsoleCommand command, ConsoleCaller caller) {
		caller.sendMessage("\"%s\" %d/%d (%s) - %s", command.getName(), command.getMinArgsCount(), 
				command.getMaxArgsCount(), command.getUsage(), command.getDescription());
	}

	public void doHelpCommand(int pageNumber, String cmd, ConsoleCaller caller) {
		if (cmd != null) {
			ConsoleCommand command = this.getCommandByName(cmd);

			if (command != null) {
				this.printConCmdHelp(command, caller);
			} else {
				caller.sendMessage("&8(&d!&8)&c Unrecognized command \"%s\"!", cmd);
			}
		} else {
			if (this.commands.size() < 1) {
				caller.sendMessage("&8(&d!&8)&c There are no commands available!");
				return;
			} else if (this.commands.size() >= 10) {
				int commandsProccessed = 0;

				for (int commandIndex = 10 * pageNumber; commandIndex < this.commands.size(); commandIndex++) {
					commandsProccessed++;
					if (commandsProccessed > 10) {						
						break;
					}

					ConsoleCommand command = this.commands.get(commandIndex);
					this.printConCmdHelp(command, caller);
				}
			} else {
				for (ConsoleCommand command : this.commands) {
					this.printConCmdHelp(command, caller);
				}
			}

			caller.sendMessage("Help format: name minargs/maxargs (usage) - description");
			caller.sendMessage("Viewing page %d/%d", pageNumber, this.commands.size() / 10);
		}
	}
}