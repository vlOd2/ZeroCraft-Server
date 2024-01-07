package org.zerocraft.server.consolehandler;

import org.zerocraft.server.ZeroCraftServer;

public interface ConsoleCommand {
    public String getName();
    
    public default String[] getAliases() {
    	return new String[0];
    }
    
    public String getDescription();
    
    public default String getRequiredPermission() {
    	return "none";
    }
    
    public String getUsage();
    
    public int getMinArgsCount();
    
    public int getMaxArgsCount();
    
    public void execute(ZeroCraftServer server, ConsoleCaller caller, String[] args) throws Exception;
}