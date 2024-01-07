package org.zerocraft.server.configuration;

public class MainConfig implements Config {
	public static MainConfig instance;
	public int listenPort = 25565;
	public String listenIP = "0.0.0.0";
	public boolean useWhiteList = false;
	public boolean showVerboseLogs = false;
	public int maxUsers = 64;
}
