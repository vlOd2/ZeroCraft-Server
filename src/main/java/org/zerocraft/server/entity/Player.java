package org.zerocraft.server.entity;

import org.zerocraft.server.level.Level;
import org.zerocraft.server.net.NetServerHandler;

public class Player extends Entity {
	private static final long serialVersionUID = 0L;
	public NetServerHandler netHandler;
	public byte playerID;

	public Player(Level level, NetServerHandler netHandler, byte playerID) {
		super(level);
		this.netHandler = netHandler;
		this.playerID = playerID;
		this.heightOffset = 1.62F;
	}

	public void changeLevel(Level level) {
		this.level = level;
		this.setPos(0, 0, 0);
		this.resetToSpawn();
	}
}