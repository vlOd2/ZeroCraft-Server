package org.zerocraft.server.level.tile;

import java.util.Random;

import org.zerocraft.server.level.Level;

public class GrassTile extends Tile {
	protected GrassTile(int id) {
		super(id);
		this.setTicking(true);
	}

	@Override
	public void tick(Level level, int x, int y, int z, Random random) {
		if (random.nextInt(4) == 0) {
			if (!level.isTileLit(x, y + 1, z)) {
				level.setTile(x, y, z, Tile.dirt.id);
			} else {
				for (int i = 0; i < 4; i++) {
					int randX = x + random.nextInt(3) - 1;
					int randY = y + random.nextInt(5) - 3;
					int randZ = z + random.nextInt(3) - 1;

					if (level.getTile(randX, randY, randZ) == Tile.dirt.id && 
						level.isTileLit(randX, randY + 1, randZ)) {
						level.setTile(randX, randY, randZ, Tile.grass.id);
					}
				}
			}
		}
	}
}