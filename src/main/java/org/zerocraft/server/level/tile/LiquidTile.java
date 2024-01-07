package org.zerocraft.server.level.tile;

import java.util.Random;

import org.zerocraft.server.entity.AABB;
import org.zerocraft.server.level.Level;

public class LiquidTile extends Tile {
	protected Liquid liquid;
	protected int calmTileID;
	protected int tileID;

	protected LiquidTile(int id, Liquid liquid) {
		super(id);
		
		this.liquid = liquid;
		this.tileID = id;
		this.calmTileID = id + 1;
		float f4 = 0.01F;
		float f3 = 0.1F;
		this.setShape(f4 + 0.0F, 0.0F - f3 + f4, f4 + 0.0F, f4 + 1.0F, 1.0F - f3 + f4, f4 + 1.0F);
		this.setTicking(true);

		if (liquid == Liquid.lava) {
			this.setTickSpeed(16);
		}
	}

	@Override
	public void onBlockAdded(Level level, int x, int y, int z) {
		level.addToTick(x, y, z, this.tileID);
	}

	@Override
	public void tick(Level level, int x, int y, int z, Random random) {
		LiquidTile liquidTile8 = this;
		boolean updated = false;

		boolean canUpdate;
		do {
			--y;
			if (level.getTile(x, y, z) != 0 || !liquidTile8.checkSponge(level, x, y, z)) {
				break;
			}

			if (canUpdate = level.setTile(x, y, z, liquidTile8.tileID)) {
				updated = true;
			}
		} while (canUpdate && liquidTile8.liquid != Liquid.lava);

		++y;
		if (liquidTile8.liquid == Liquid.water || !updated) {
			updated = updated | liquidTile8.checkWater(level, x - 1, y, z) | liquidTile8.checkWater(level, x + 1, y, z)
					| liquidTile8.checkWater(level, x, y, z - 1) | liquidTile8.checkWater(level, x, y, z + 1);
		}

		if (!updated) {
			level.setTileRaw(x, y, z, liquidTile8.calmTileID);
		} else {
			level.addToTick(x, y, z, liquidTile8.tileID);
		}
	}

	private boolean checkSponge(Level level, int x, int y, int z) {
		if (this.liquid == Liquid.water) {
			for (int x2 = x - 2; x2 <= x + 2; ++x2) {
				for (int y2 = y - 2; y2 <= y + 2; ++y2) {
					for (int z2 = z - 2; z2 <= z + 2; ++z2) {
						if (level.getTile(x2, y2, z2) == Tile.sponge.id) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	private boolean checkWater(Level level, int x, int y, int z) {
		if (level.getTile(x, y, z) == 0) {
			if (!this.checkSponge(level, x, y, z)) {
				return false;
			}

			if (level.setTile(x, y, z, this.tileID)) {
				level.addToTick(x, y, z, this.tileID);
			}
		}

		return false;
	}

	@Override
	public AABB getAABB(int x, int y, int z) {
		return null;
	}

	@Override
	public boolean isOpaque() {
		return true;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public Liquid getLiquidType() {
		return this.liquid;
	}

	@Override
	public void neighborChanged(Level level, int x, int y, int z, int neighborID) {
		if (neighborID != 0) {
			Liquid liquid = Tile.tiles[neighborID].getLiquidType();
			if (this.liquid == Liquid.water && liquid == Liquid.lava
					|| liquid == Liquid.water && this.liquid == Liquid.lava) {
				level.setTile(x, y, z, Tile.stone.id);
				return;
			}
		}

		level.addToTick(x, y, z, neighborID);
	}

	@Override
	public int getTickDelay() {
		return this.liquid == Liquid.lava ? 5 : 0;
	}
}