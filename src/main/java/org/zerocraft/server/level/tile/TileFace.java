package org.zerocraft.server.level.tile;

/**
 * Represents the face of a tile
 */
public enum TileFace {
	DOWN(0),
	UP(1),
	NORTH(2),
	SOUTH(3),
	WEST(4),
	EAST(5),
	NONE(15);

	public int index;

	TileFace(int index) {
		this.index = index;
	}
}
