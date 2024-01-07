package org.zerocraft.server.level.tile;

import org.zerocraft.server.level.Level;

public class SpongeTile extends Tile {
	protected SpongeTile(int id) {
		super(id);
	}

	@Override
	public void onPlace(Level level, int x, int y, int z) {
		for (int i7 = x - 2; i7 <= x + 2; ++i7) {
			for (int i5 = y - 2; i5 <= y + 2; ++i5) {
				for (int i6 = z - 2; i6 <= z + 2; ++i6) {
					if (level.isTileWater(i7, i5, i6)) {
						level.setTileNoNotify(i7, i5, i6, 0);
					}
				}
			}
		}

	}

	@Override
	public void onBreak(Level level, int x, int y, int z) {
		for (int i7 = x - 2; i7 <= x + 2; ++i7) {
			for (int i5 = y - 2; i5 <= y + 2; ++i5) {
				for (int i6 = z - 2; i6 <= z + 2; ++i6) {
					level.notifyNeighbors(i7, i5, i6, level.getTile(i7, i5, i6));
				}
			}
		}
	}
}