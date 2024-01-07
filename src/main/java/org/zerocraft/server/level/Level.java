package org.zerocraft.server.level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.zerocraft.server.ZeroCraftServer;
import org.zerocraft.server.entity.AABB;
import org.zerocraft.server.entity.Entity;
import org.zerocraft.server.level.tile.Liquid;
import org.zerocraft.server.level.tile.Tile;
import org.zerocraft.server.net.packet.PacketUpdateTile;

/**
 * Represents a game level
 */
public class Level {
	public ZeroCraftServer instance;
	private Random random = new Random();
	public int width;
	public int height;
	public int depth;
	public byte[] tiles;
	private int[] lightDepths;
	private ArrayList<TickTileInfo> tickList = new ArrayList<>();
	public ArrayList<Entity> entities = new ArrayList<>();
	private int tickCount;
	private int tickRandValue = this.random.nextInt();
	public int tickUnprocessedTiles;
	public String name;
	public String creator;
	public long createTime;
	public int spawnX;
	public int spawnY;
	public int spawnZ;
	public float spawnYaw;
	public float spawnPitch;
	public int skyColor = 0x99CCFF;
	public int fogColor = 0xFFFFFF;
	public int cloudColor = 0xFFFFFF;

	/**
	 * Initializes this level after being loaded from a serialized form
	 */
	public void initAfterLoadFile() {
		if (this.tiles == null) {
			throw new RuntimeException("The level is corrupt!");
		} else {
			this.lightDepths = new int[this.width * this.depth];
			this.random = new Random();
			this.tickRandValue = this.random.nextInt();
			this.tickList = new ArrayList<>();

			Arrays.fill(this.lightDepths, this.height);
			this.calcLightDepths(0, 0, this.width, this.depth);
			
			if (this.entities == null) {
				this.entities = new ArrayList<>();
			}

			if (this.spawnX == 0 && this.spawnY == 0 && this.spawnZ == 0) {
				this.calculateSpawn();
			}
		}
	}

	/**
	 * Sets the level data
	 *
	 * @param width  the width of the level
	 * @param height the height of the level
	 * @param depth  the depth of the level
	 * @param tiles  the tiles of the level
	 */
	public void setData(int width, int height, int depth, byte[] tiles) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.tiles = tiles;
		this.lightDepths = new int[width * depth];
		
		Arrays.fill(this.lightDepths, this.height);
		this.calcLightDepths(0, 0, width, depth);
		this.tickList.clear();
		
		System.gc();
	}

	/**
	 * Calculates the spawn position
	 */
	public void calculateSpawn() {
		Random random = new Random();
		int iterations = 0;

		int x;
		int z;
		int highestTile;
		do {
			++iterations;
			x = random.nextInt(this.width / 2) + this.width / 4;
			z = random.nextInt(this.depth / 2) + this.depth / 4;
			highestTile = this.getTopOfHighestTile(x, z) + 1;

			if (iterations == 10000) {
				this.spawnX = x;
				this.spawnY = -100;
				this.spawnZ = z;
				return;
			}
		} while (highestTile <= this.getWaterLevel());

		this.spawnX = x;
		this.spawnY = highestTile;
		this.spawnZ = z;
	}

	/**
	 * Calculates the light depths of the specified area
	 *
	 * @param x0 the starting x
	 * @param z0 the starting z
	 * @param x1 the ending x
	 * @param z1 the ending z
	 */
	public void calcLightDepths(int x0, int z0, int x1, int z1) {
		for (int x = x0; x < x0 + x1; x++) {
			for (int z = z0; z < z0 + z1; z++) {
				int nLight;
				for (nLight = this.height - 1; nLight > 0 && !this.isTileOpaque(x, nLight, z); nLight--) {
				}

				this.lightDepths[x + z * this.width] = nLight + 1;
			}
		}
	}
	
	/**
	 * Checks if the specified tile is opaque (blocks light)
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @return true if the tile is opaque, false if otherwise
	 */
	public boolean isTileOpaque(int x, int y, int z) {
		Tile tile = Tile.tiles[this.getTile(x, y, z)];
		return tile == null ? false : tile.isOpaque();
	}

	/**
	 * Gets the level cubes around the specified {@link AABB}
	 *
	 * @param aabb the AABB to get the cubes around
	 * @return the cubes
	 */
	public ArrayList<AABB> getLevelCubes(AABB aabb) {
		ArrayList<AABB> cubeList = new ArrayList<>();
		int startX = (int) aabb.x0;
		int endX = (int) aabb.x1 + 1;
		int startY = (int) aabb.y0;
		int endY = (int) aabb.y1 + 1;
		int startZ = (int) aabb.z0;
		int endZ = (int) aabb.z1 + 1;

		if (aabb.x0 < 0.0F) {
			--startX;
		}

		if (aabb.y0 < 0.0F) {
			--startY;
		}

		if (aabb.z0 < 0.0F) {
			--startZ;
		}

		for (int x = startX; x < endX; ++x) {
			for (int y = startY; y < endY; ++y) {
				for (int z = startZ; z < endZ; ++z) {
					if (!this.isInLevelBounds(x, y, z)) {
						if (x < 0 || y < 0 || z < 0 || x >= this.width || z >= this.depth) {
							AABB borderAABB = Tile.bedrock.getAABB(x, y, z);
							cubeList.add(borderAABB);
						}
						continue;
					}

					Tile tile = Tile.tiles[this.getTile(x, y, z)];
					AABB tileAABB = tile != null ? tile.getAABB(x, y, z) : null;
					if (tileAABB != null) {
						cubeList.add(tileAABB);
					}
				}
			}
		}

		return cubeList;
	}

	/**
	 * Swaps a specified tile with another
	 *
	 * @param firstTileX  the x of the swapped tile
	 * @param firstTileY  the y of the swapped tile
	 * @param firstTileZ  the z of the swapped tile
	 * @param secondTileX the x of the tile to swap with
	 * @param secondTileY the y of the tile to swap with
	 * @param secondTileZ the z of the tile to swap with
	 */
	public void swapTiles(int firstTileX, int firstTileY, int firstTileZ, int secondTileX, int secondTileY,
			int secondTileZ) {
		int firstTile = this.getTile(firstTileX, firstTileY, firstTileZ);
		int secondTile = this.getTile(secondTileX, secondTileY, secondTileZ);
		this.setTileNoNotify(firstTileX, firstTileY, firstTileZ, secondTile);
		this.setTileNoNotify(secondTileX, secondTileY, secondTileZ, firstTile);
		this.notifyNeighbors(firstTileX, firstTileY, firstTileZ, secondTile);
		this.notifyNeighbors(secondTileX, secondTileY, secondTileZ, firstTile);
	}

	/**
	 * Sets a tile without notifying the neighbors<br>
	 *
	 * @param x  the x
	 * @param y  the y
	 * @param z  the z
	 * @param id the ID to set the tile to
	 * @return true if the tile was changed, false if otherwise
	 */
	public boolean setTileNoNotify(int x, int y, int z, int id) {
		if (!this.isInLevelBounds(x, y, z) || id == this.tiles[(y * this.depth + z) * this.width + x]) {
			return false;
		}

		byte existingID = this.tiles[(y * this.depth + z) * this.width + x];
		this.setTileRaw(x, y, z, id);

		if (existingID != 0) {
			Tile.tiles[existingID].onBreak(this, x, y, z);
		}

		if (id != 0) {
			Tile.tiles[id].onPlace(this, x, y, z);
		}

		this.calcLightDepths(x, z, 1, 1);
		
		return true;
	}

	/**
	 * Sets a tile and notifies the neighbors
	 *
	 * @param x  the x
	 * @param y  the y
	 * @param z  the z
	 * @param id the ID to set tile to
	 * @return true if the tile was changed, false if otherwise
	 */
	public boolean setTile(int x, int y, int z, int id) {
		if (this.setTileNoNotify(x, y, z, id)) {
			this.notifyNeighbors(x, y, z, id);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Notifies the neighbors that the specified tile changed
	 *
	 * @param x  the x of the changed tile
	 * @param y  the y of the changed tile
	 * @param z  the z of the changed tile
	 * @param id the new ID of the changed tile
	 */
	public void notifyNeighbors(int x, int y, int z, int id) {
		this.notifyOfNeighborChange(x - 1, y, z, id);
		this.notifyOfNeighborChange(x + 1, y, z, id);
		this.notifyOfNeighborChange(x, y - 1, z, id);
		this.notifyOfNeighborChange(x, y + 1, z, id);
		this.notifyOfNeighborChange(x, y, z - 1, id);
		this.notifyOfNeighborChange(x, y, z + 1, id);
	}

	/**
	 * Sets a tile without any updates
	 *
	 * @param x  the x
	 * @param y  the y
	 * @param z  the z
	 * @param id the ID to set the tile to
	 * @return true if the tile was changed, false if otherwise
	 */
	public boolean setTileRaw(int x, int y, int z, int id) {
		if (!this.isInLevelBounds(x, y, z) || id == this.tiles[(y * this.depth + z) * this.width + x]) {
			return false;
		}

		this.tiles[(y * this.depth + z) * this.width + x] = (byte) id;
		this.instance.sendGlobalPacket(new PacketUpdateTile((short)x, (short)y, (short)z, 
				(byte) this.getTile(x, y, z)));
		
		return true;
	}

	private void notifyOfNeighborChange(int x, int y, int z, int neighborID) {
		if (!this.isInLevelBounds(x, y, z)) {
			return;
		}
		Tile tile = Tile.tiles[this.tiles[(y * this.depth + z) * this.width + x]];

		if (tile != null) {
			tile.neighborChanged(this, x, y, z, neighborID);
		}
	}

	/**
	 * Checks if the specified tile is lit
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @return true if the tile is lit, false if otherwise
	 */
	public boolean isTileLit(int x, int y, int z) {
		return this.isInLevelBounds(x, y, z) ? y >= this.lightDepths[x + z * this.width] : true;
	}
	
	/**
	 * Gets the specified tile
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @return the ID or 0 if no tile
	 */
	public int getTile(int x, int y, int z) {
		return this.isInLevelBounds(x, y, z) ? this.tiles[(y * this.depth + z) * this.width + x] & 255 : 0;
	}

	/**
	 * Checks if you can place a tile at the specified position
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @return true if you can place, false ife you can't
	 */
	public boolean canPlaceTileAt(int x, int y, int z) {
		if (!this.isInLevelBounds(x, y, z)) {
			return false;
		}
		Tile existingTile = Tile.tiles[this.getTile(x, y, z)];

		return existingTile == null || existingTile == Tile.water || existingTile == Tile.calmWater
				|| existingTile == Tile.lava || existingTile == Tile.calmLava;
	}

	/**
	 * Checks if the specified tile is solid
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @return true if it is solid, false if it isn't
	 */
	public boolean isTileSolid(int x, int y, int z) {
		Tile tile = Tile.tiles[this.getTile(x, y, z)];
		return tile == null ? false : tile.isSolid();
	}

	/**
	 * Ticks the entities (this also removes them if they are dead)
	 */
	public void tickEntities() {
		for (int i = 0; i < this.entities.size(); i++) {
			this.entities.get(i).tick();

			if (this.entities.get(i).dead) {
				this.entities.remove(i--);
			}
		}
	}

	/**
	 * Ticks the level
	 */
	public void tick() {
		this.tickCount++;

		int maxWidthShift = 1;
		int maxWidth = 1;
		while (maxWidth << maxWidthShift < this.width) {
			maxWidthShift++;
		}

		int maxDepthShift = 1;
		while (1 << maxDepthShift < this.depth) {
			maxDepthShift++;
		}

		int maxDepthIndex = this.depth - 1;
		int maxWidthIndex = this.width - 1;
		int maxHeightIndex = this.height - 1;

		if (this.tickCount % 5 == 0) {
			int tickListSize = this.tickList.size();

			for (int i = 0; i < tickListSize; ++i) {
				TickTileInfo tileInfo = this.tickList.remove(0);

				if (tileInfo.scheduledTime > 0) {
					tileInfo.scheduledTime--;
					this.tickList.add(tileInfo);
				} else {
					if (isInLevelBounds(tileInfo.x, tileInfo.y, tileInfo.z)) {
						byte tileId = this.tiles[(tileInfo.y * this.depth + tileInfo.z) * this.width + tileInfo.x];
						if (tileId == tileInfo.id && tileId > 0) {
							Tile.tiles[tileId].tick(this, tileInfo.x, tileInfo.y, tileInfo.z, this.random);
						}
					}
				}
			}
		}

		this.tickUnprocessedTiles += this.width * this.depth * this.height;
		int tilesToProcess = this.tickUnprocessedTiles / 200;
		this.tickUnprocessedTiles -= tilesToProcess * 200;

		for (int i = 0; i < tilesToProcess; ++i) {
			this.tickRandValue = this.tickRandValue * 3 + 1013904223;

			int randomValue = this.tickRandValue >> 2;
			int x = randomValue & maxWidthIndex;
			int y = randomValue >> maxWidthShift & maxDepthIndex;
			int z = randomValue >> maxWidthShift + maxDepthShift & maxHeightIndex;

			byte tileId = this.tiles[(z * this.depth + y) * this.width + x];
			if (Tile.shouldTick[tileId]) {
				Tile.tiles[tileId].tick(this, x, z, y, this.random);
			}
		}
	}

	/**
	 * Checks if the specified tile position is within the level bounds
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @return true if it is, false if it isn't
	 */
	public boolean isInLevelBounds(int x, int y, int z) {
		return x >= 0 && y >= 0 && z >= 0 && x < this.width && y < this.height && z < this.depth;
	}

	/**
	 * Gets the Y position where the ground level starts
	 *
	 * @return the Y position
	 */
	public float getGroundLevel() {
		return this.height / 2 - 2;
	}

	/**
	 * Gets the Y position where the water level starts
	 *
	 * @return the Y position
	 */
	public float getWaterLevel() {
		return this.height / 2;
	}

	/**
	 * Checks if the specified {@link AABB} intersects with any liquid
	 *
	 * @param aabb the AABB
	 * @return true if it intersects, false if otherwise
	 */
	public boolean intersectsWithLiquid(AABB aabb) {
		int x0 = (int) aabb.x0;
		int x1 = (int) aabb.x1 + 1;
		int y0 = (int) aabb.y0;
		int y1 = (int) aabb.y1 + 1;
		int z0 = (int) aabb.z0;
		int z1 = (int) aabb.z1 + 1;

		if (aabb.x0 < 0.0F) {
			--x0;
		}

		if (aabb.y0 < 0.0F) {
			--y0;
		}

		if (aabb.z0 < 0.0F) {
			--z0;
		}

		if (x0 < 0) {
			x0 = 0;
		}

		if (y0 < 0) {
			y0 = 0;
		}

		if (z0 < 0) {
			z0 = 0;
		}

		if (x1 > this.width) {
			x1 = this.width;
		}

		if (y1 > this.height) {
			y1 = this.height;
		}

		if (z1 > this.depth) {
			z1 = this.depth;
		}

		for (int x = x0; x < x1; x++) {
			for (int y = y0; y < y1; y++) {
				for (int z = z0; z < z1; z++) {
					Tile tile = Tile.tiles[this.getTile(x, y, z)];

					if (tile != null && tile.getLiquidType() != Liquid.none) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Checks if the specified {@link AABB} intersects with the specified liquid
	 *
	 * @param aabb the AABB
	 * @return true if it intersects, false if otherwise
	 */
	public boolean intersectsWithLiquid(AABB aabb, Liquid liquid) {
		int x0 = (int) aabb.x0;
		int x1 = (int) aabb.x1 + 1;
		int y0 = (int) aabb.y0;
		int y1 = (int) aabb.y1 + 1;
		int z0 = (int) aabb.z0;
		int z1 = (int) aabb.z1 + 1;

		if (aabb.x0 < 0.0F) {
			--x0;
		}

		if (aabb.y0 < 0.0F) {
			--y0;
		}

		if (aabb.z0 < 0.0F) {
			--z0;
		}

		if (x0 < 0) {
			x0 = 0;
		}

		if (y0 < 0) {
			y0 = 0;
		}

		if (z0 < 0) {
			z0 = 0;
		}

		if (x1 > this.width) {
			x1 = this.width;
		}

		if (y1 > this.height) {
			y1 = this.height;
		}

		if (z1 > this.depth) {
			z1 = this.depth;
		}

		for (int x = x0; x < x1; x++) {
			for (int y = y0; y < y1; y++) {
				for (int z = z0; z < z1; z++) {
					Tile tile = Tile.tiles[this.getTile(x, y, z)];

					if (tile != null && tile.getLiquidType() == liquid) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Adds the specified tile to the tick list
	 *
	 * @param x  the x
	 * @param y  the y
	 * @param z  the z
	 * @param id the ID of the tile
	 */
	public void addToTick(int x, int y, int z, int id) {
		TickTileInfo info = new TickTileInfo(x, y, z, id);
		
		if (id > 0) {
			info.scheduledTime = Tile.tiles[id].getTickDelay();
		}

		this.tickList.add(info);
	}

	/**
	 * Checks if the specified {@link AABB} does not intersect any entities
	 *
	 * @param aabb the AABB
	 * @return true if isn't intersecting, false if otherwise
	 */
	public boolean isNotIntersectingWithEntity(AABB aabb) {
		for (Entity entity : this.entities) {
			if (entity.boundingBox.intersects(aabb)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if there is any solid block in the specified range
	 *
	 * @param x     the x of the center
	 * @param y     the y of the center
	 * @param z     the z of the center
	 * @param range the range around the center
	 * @return true if there is any solid tile, false if otherwise
	 */
	public boolean isSolidRange(float x, float y, float z, float range) {
		return this.isTileSolid(x - range, y - range, z - range) ? true
				: this.isTileSolid(x - range, y - range, z + range) ? true
						: this.isTileSolid(x - range, y + range, z - range) ? true
								: this.isTileSolid(x - range, y + range, z + range) ? true
										: this.isTileSolid(x + range, y - range, z - range) ? true
												: this.isTileSolid(x + range, y - range, z + range) ? true
														: this.isTileSolid(x + range, y + range, z - range) ? true
																: this.isTileSolid(x + range, y + range, z + range);
	}

	private boolean isTileSolid(float x, float y, float z) {
		int tileID = this.getTile((int) x, (int) y, (int) z);
		return tileID > 0 && Tile.tiles[tileID].isSolid();
	}

	/**
	 * Gets the top of the highest solid non-liquid tile<br>
	 *
	 * Example: If we have a tile at X:0 Y:5 Z:0 and we pass X:0 Z:0, this will
	 * return Y:6
	 *
	 * @param x the x
	 * @param z the z
	 * @return the Y of the highest tile or 0 if none found
	 */
	public int getTopOfHighestTile(int x, int z) {
		int y = this.height;

		while (y > 0) {
			int tileID = this.getTile(x, y - 1, z);

			if (tileID == 0 || Tile.tiles[tileID].getLiquidType() != Liquid.none) {
				y--;
				continue;
			}

			break;
		}

		return y;
	}

	/**
	 * Sets the spawn of this world
	 *
	 * @param x     the x
	 * @param y     the y
	 * @param z     the z
	 * @param yaw   the yaw
	 * @param pitch the pitch
	 */
	public void setSpawn(int x, int y, int z, float yaw, float pitch) {
		this.spawnX = x;
		this.spawnY = y;
		this.spawnZ = z;
		this.spawnYaw = yaw;
		this.spawnPitch = pitch;
	}

	/**
	 * Creates a copy of the tiles
	 *
	 * @return the copy
	 */
	public byte[] copyTiles() {
		return Arrays.copyOf(this.tiles, this.tiles.length);
	}

	/**
	 * Checks if the specified tile is water
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @return true if the tile is water, false if otherwise
	 */
	public boolean isTileWater(int x, int y, int z) {
		Tile tile = Tile.tiles[this.getTile(x, y, z)];
		return tile != null && tile.getLiquidType() == Liquid.water;
	}
}