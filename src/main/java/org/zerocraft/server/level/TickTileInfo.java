package org.zerocraft.server.level;

/**
 * Holds information about a tile that ticks
 */
public class TickTileInfo {
	public int x;
	public int y;
	public int z;
	public int id;
	public int scheduledTime;

	public TickTileInfo(int x, int y, int z, int id) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}
}