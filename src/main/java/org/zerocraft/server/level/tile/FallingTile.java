package org.zerocraft.server.level.tile;

import org.zerocraft.server.level.Level;

public class FallingTile extends Tile {
	public FallingTile(int id) {
		super(id);
	}

	@Override
	public void onBlockAdded(Level level, int x, int y, int z) {
		this.tryToFall(level, x, y, z);
	}

	@Override
	public void neighborChanged(Level level, int x, int y, int z, int neighborID) {
		this.tryToFall(level, x, y, z);
	}

	private void tryToFall(Level level, int x, int y, int z) {
		int currentX = x;
		int currentY = y;
		int currentZ = z;

		while (true) {
			int yBelow = currentY - 1;
			int tileBelow = level.getTile(currentX, yBelow, currentZ);
			Liquid tileBelowLiquid = tileBelow != 0 ? Tile.tiles[tileBelow].getLiquidType() : null;
			boolean tileBelowSuitable = tileBelow == 0 ? true
					: tileBelowLiquid == Liquid.water ? true : tileBelowLiquid == Liquid.lava;

			if (!tileBelowSuitable || currentY <= 0) {
				if (currentY != y) {
					level.swapTiles(x, y, z, currentX, currentY, currentZ);
				}

				return;
			}

			--currentY;
		}
	}
}