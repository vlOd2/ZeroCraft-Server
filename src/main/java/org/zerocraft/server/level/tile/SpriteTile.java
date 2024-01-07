package org.zerocraft.server.level.tile;

import org.zerocraft.server.entity.AABB;

public class SpriteTile extends Tile {
	protected SpriteTile(int id, float width, float height) {
		super(id);
		this.setShape(0.5F - width, 0.0F, 0.5F - width, 0.5F + width, height, 0.5F + width);
	}

	@Override
	public AABB getAABB(int x, int y, int z) {
		return null;
	}

	@Override
	public boolean isOpaque() {
		return false;
	}

	@Override
	public boolean isSolid() {
		return false;
	}
}