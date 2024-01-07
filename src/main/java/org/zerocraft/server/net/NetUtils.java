package org.zerocraft.server.net;

import org.zerocraft.server.configuration.BannedConfig;
import org.zerocraft.server.configuration.MainConfig;
import org.zerocraft.server.configuration.WhitelistConfig;

public class NetUtils {
	public static final String USERNAME_REGEX_CHECK = "^(?=.{3,16}$)[a-zA-Z0-9_]+$";
	
	public static boolean performModerationChecks(NetBaseHandler handler, String username) {
    	if (MainConfig.instance.useWhiteList && 
    		!WhitelistConfig.instance.ips.contains(handler.netManager.getAddress().ip) &&
    		!WhitelistConfig.instance.users.contains(username)) {
    		handler.kick("You are not white-listed!");
    	}
    	
		String bannedReason = BannedConfig.instance.users.get(username);
		String bannedReasonIP = BannedConfig.instance.ips.get(handler.netManager.getAddress().ip);
    	
    	if (bannedReason != null) {
    		handler.kick("You are banned: " + bannedReason);
    		return false;
    	} else if (bannedReasonIP != null) {
    		handler.kick("You are IP banned: " + bannedReasonIP);
    		return false;
    	}
    	
    	return true;
	}
	
	public static boolean performNameVerification(NetBaseHandler handler, String username) {
    	if (!username.matches(USERNAME_REGEX_CHECK)) {
    		handler.kick("Illegal username!");
    		return false;
    	}
    	
    	return true;
	}
}
