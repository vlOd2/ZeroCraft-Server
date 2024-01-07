package org.zerocraft.server.level.tile;

import java.util.Random;

import org.zerocraft.server.level.Level;

public class MushroomTile extends SpriteTile {
	protected MushroomTile(int id) {
		super(id, 0.125F, 0.4F);
		this.setTicking(true);
	}

	@Override
	public void tick(Level level, int x, int y, int z, Random random) {
		int bellowTile = level.getTile(x, y - 1, z);

		if (!level.isTileLit(x, y, z) || bellowTile != Tile.dirt.id && bellowTile != Tile.grass.id) {
			level.setTile(x, y, z, 0);
		}
	}

	@Override
	public void onPlace(Level level, int x, int y, int z) {
		int bellowTile = level.getTile(x, y - 1, z);

		if (!level.isTileLit(x, y, z) || bellowTile != Tile.dirt.id && bellowTile != Tile.grass.id) {
			level.setTile(x, y, z, 0);
		}
	}
}
