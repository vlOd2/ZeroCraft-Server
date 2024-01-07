package org.zerocraft.server.entity;

import java.io.Serializable;

import org.zerocraft.server.level.Level;
import org.zerocraft.server.level.tile.Liquid;
import org.zerocraft.server.level.tile.Tile;

/**
 * An entity
 */
public abstract class Entity implements Serializable {
	private static final long serialVersionUID = 0L;
	protected Level level;
	public float prevX;
	public float prevY;
	public float prevZ;
	public float x;
	public float y;
	public float z;
	public float motionX;
	public float motionY;
	public float motionZ;
	public float yaw;
	public float pitch;
	public float prevYaw;
	public float prevPitch;
	public AABB boundingBox;
	public boolean onGround;
	public boolean horizontalCollision;
	public boolean dead;
	public float heightOffset;
	protected float bbWidth = 0.6F;
	public float bbHeight = 1.8F;
	public float ySlideOffset;
	public float footSize;
	public boolean noClip;

	public Entity(Level level) {
		this.level = level;
		if (this.level == null) {
			throw new IllegalArgumentException("Level cannot be null");
		}
		this.setPos(0.0F, 0.0F, 0.0F);
	}

	/**
	 * Resets the position to the spawn of the world
	 */
	public void resetToSpawn() {
		float f1 = this.level.spawnX + 0.5F;
		float f2 = this.level.spawnY;

		for (float f3 = this.level.spawnZ + 0.5F; f2 > 0.0F; ++f2) {
			this.setPos(f1, f2, f3);

			if (this.level.getLevelCubes(this.boundingBox).size() == 0) {
				break;
			}
		}

		this.motionX = this.motionY = this.motionZ = 0.0F;
		this.yaw = this.level.spawnYaw;
		this.pitch = this.level.spawnPitch;
	}

	/**
	 * Marks this entity as dead
	 */
	public void kill() {
		this.dead = true;
	}

	/**
	 * Sets the size of the bounding box
	 *
	 * @param width  the width
	 * @param height the height
	 */
	public void setSize(float width, float height) {
		this.bbWidth = width;
		this.bbHeight = height;
	}

	/**
	 * Sets the rotation of this entity
	 *
	 * @param yaw   the yaw
	 * @param pitch the pitch
	 */
	public void setRot(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}

	/**
	 * Sets the position of this entity
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public void setPos(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		float bbWCenter = this.bbWidth / 2.0F;
		float bbHCenter = this.bbHeight / 2.0F;
		this.boundingBox = new AABB(x - bbWCenter, y - bbHCenter, z - bbWCenter, x + bbWCenter, y + bbHCenter,
				z + bbWCenter);
	}

	/**
	 * Sets the position and the rotation of this entity
	 *
	 * @param x     the x
	 * @param y     the y
	 * @param z     the z
	 * @param yaw   the yaw
	 * @param pitch the pitch
	 */
	public void setPosRot(float x, float y, float z, float yaw, float pitch) {
		this.prevX = this.x = x;
		this.prevY = this.y = y;
		this.prevZ = this.z = z;
		this.setRot(yaw, pitch);
		this.setPos(x, y, z);
	}

	/**
	 * Turns this entity using the specified amounts
	 *
	 * @param yaw   the amount to turn the yaw
	 * @param pitch the amount to turn the pitch
	 */
	public void turn(float yaw, float pitch) {
		float prevPitch = this.pitch;
		float prevYaw = this.yaw;
		this.yaw = (float) (this.yaw + yaw * 0.15D);
		this.pitch = (float) (this.pitch - pitch * 0.15D);

		if (this.pitch < -90.0F) {
			this.pitch = -90.0F;
		}

		if (this.pitch > 90.0F) {
			this.pitch = 90.0F;
		}

		this.prevPitch += this.pitch - prevPitch;
		this.prevYaw += this.yaw - prevYaw;
	}

	/**
	 * Turns (using interpolation) this entity using the specified amounts
	 *
	 * @param yaw   the amount to turn the yaw
	 * @param pitch the amount to turn the pitch
	 */
	public void interpolateTurn(float yaw, float pitch) {
		this.yaw = (float) (this.yaw + yaw * 0.15D);
		this.pitch = (float) (this.pitch - pitch * 0.15D);

		if (this.pitch < -90.0F) {
			this.pitch = -90.0F;
		}

		if (this.pitch > 90.0F) {
			this.pitch = 90.0F;
		}
	}

	/**
	 * Ticks this entity
	 */
	public void tick() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.prevPitch = this.pitch;
		this.prevYaw = this.yaw;
	}

	/**
	 * Checks if this entity were to be moved is not colliding with anything
	 *
	 * @param x the x to check at
	 * @param y the y to check at
	 * @param z the z to check at
	 * @return true if the entity is not colliding, false if otherwise
	 */
	public boolean isFree(float x, float y, float z) {
		AABB checkAABB = this.boundingBox.cloneMove(x, y, z);
		return this.level.getLevelCubes(checkAABB).size() > 0 ? false : !this.level.intersectsWithLiquid(checkAABB);
	}

	/**
	 * Gets the tile at the entity's head
	 *
	 * @return the tile or null
	 */
	public Tile getTileAtHead() {
		int id = this.level.getTile((int) this.x, (int) this.y, (int) this.z);
		return Tile.tiles[id];
	}

	/**
	 * Gets the tile at the entity's feet
	 *
	 * @return the tile or null
	 */
	public Tile getTileAtFeet() {
		int id = this.level.getTile((int) this.x, (int) (this.y - this.heightOffset), (int) this.z);
		return Tile.tiles[id];
	}

	/**
	 * Gets the tile below the entity
	 *
	 * @return the tile or null
	 */
	public Tile getTileBelow() {
		int id = this.level.getTile((int) this.x, (int) (this.y - 0.2F - this.heightOffset), (int) this.z);
		return Tile.tiles[id];
	}

	/**
	 * Checks if the entity is in water
	 *
	 * @return true if it is, false if not
	 */
	public boolean isInWater() {
		return this.level.intersectsWithLiquid(this.boundingBox.grow(0.0F, -0.4F, 0.0F), Liquid.water);
	}

	/**
	 * Checks if the entity is in lava
	 *
	 * @return true if it is, false if not
	 */
	public boolean isInLava() {
		return this.level.intersectsWithLiquid(this.boundingBox.grow(0.0F, -0.4F, 0.0F), Liquid.lava);
	}

	/**
	 * Gets the distance to the specified position from this entity
	 *
	 * @param posX the x
	 * @param posY the y
	 * @param posZ the z
	 * @return the distance
	 */
	public float distanceTo(float posX, float posY, float posZ) {
		float x = this.x - posX;
		float y = this.y - posY;
		float z = this.z - posZ;
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * Gets the distance to the specified entity from this entity
	 *
	 * @param entity the entity to check
	 * @return the distance
	 */
	public float distanceTo(Entity entity) {
		float x = this.x - entity.x;
		float y = this.y - entity.y;
		float z = this.z - entity.z;
		return (float) Math.sqrt(x * x + y * y + z * z);
	}
}