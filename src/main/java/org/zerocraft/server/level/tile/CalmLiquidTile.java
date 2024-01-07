package org.zerocraft.server.level.tile;

import java.util.Random;

import org.zerocraft.server.level.Level;

public class CalmLiquidTile extends LiquidTile {
	protected CalmLiquidTile(int id, Liquid liquid) {
		super(id, liquid);
		this.tileID = id - 1;
		this.calmTileID = id;
		this.setTicking(false);
	}

	@Override
	public void tick(Level level, int x, int y, int z, Random random) {
	}

	@Override
	public void neighborChanged(Level level, int x, int y, int z, int neighborID) {
		boolean updated = false;

		if (level.getTile(x - 1, y, z) == 0) {
			updated = true;
		}

		if (level.getTile(x + 1, y, z) == 0) {
			updated = true;
		}

		if (level.getTile(x, y, z - 1) == 0) {
			updated = true;
		}

		if (level.getTile(x, y, z + 1) == 0) {
			updated = true;
		}

		if (level.getTile(x, y - 1, z) == 0) {
			updated = true;
		}

		if (neighborID != 0) {
			Liquid liquid7 = Tile.tiles[neighborID].getLiquidType();
			if (this.liquid == Liquid.water && liquid7 == Liquid.lava
					|| liquid7 == Liquid.water && this.liquid == Liquid.lava) {
				level.setTile(x, y, z, Tile.stone.id);
				return;
			}
		}

		if (updated) {
			level.setTileRaw(x, y, z, this.tileID);
			level.addToTick(x, y, z, this.tileID);
		}
	}
}