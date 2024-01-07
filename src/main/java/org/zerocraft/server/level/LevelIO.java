package org.zerocraft.server.level;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.zerocraft.server.ZeroCraftServer;
import org.zerocraft.server.nbt.NBTCompoundIO;
import org.zerocraft.server.nbt.NBTTagCompound;

/**
 * Input/Output operations for a {@link Level}
 */
public class LevelIO {
	/**
	 * Loads a saved level alongside the player
	 *
	 * @param gameInstance the instance of the game
	 * @param inputStream  the input stream to load from
	 * @return a tuple containing the level and the player
	 */
	public static Level load(InputStream inputStream) {
		try {
			NBTTagCompound levelNBT = NBTCompoundIO.read(inputStream);

			Level level = new Level();
			level.name = levelNBT.getString("name");
			level.creator = levelNBT.getString("creator");
			level.createTime = levelNBT.getLong("createTime");
			level.spawnX = levelNBT.getInteger("spawnX");
			level.spawnY = levelNBT.getInteger("spawnY");
			level.spawnZ = levelNBT.getInteger("spawnZ");
			level.spawnYaw = levelNBT.getFloat("spawnYaw");
			level.spawnPitch = levelNBT.getFloat("spawnPitch");
			level.skyColor = levelNBT.getInteger("skyColor");
			level.fogColor = levelNBT.getInteger("fogColor");
			level.cloudColor = levelNBT.getInteger("cloudColor");
			level.width = levelNBT.getInteger("width");
			level.height = levelNBT.getInteger("height");
			level.depth = levelNBT.getInteger("depth");
			level.tiles = levelNBT.getByteArray("data");
			level.initAfterLoadFile();

			return level;
		} catch (Exception ex) {
			ZeroCraftServer.logger.throwable(ex);
			return null;
		}
	}

	/**
	 * Saves the specified level alongside the player
	 *
	 * @param level        the level to save
	 * @param player       the player to save
	 * @param outputStream the output stream to save to
	 */
	public static void save(Level level, OutputStream outputStream) {
		try {
			NBTTagCompound levelNBT = new NBTTagCompound();
			levelNBT.setString("name", level.name);
			levelNBT.setString("creator", level.creator);
			levelNBT.setLong("createTime", level.createTime);
			levelNBT.setInteger("spawnX", level.spawnX);
			levelNBT.setInteger("spawnY", level.spawnY);
			levelNBT.setInteger("spawnZ", level.spawnZ);
			levelNBT.setFloat("spawnYaw", level.spawnYaw);
			levelNBT.setFloat("spawnPitch", level.spawnPitch);
			levelNBT.setInteger("skyColor", level.skyColor);
			levelNBT.setInteger("fogColor", level.fogColor);
			levelNBT.setInteger("cloudColor", level.cloudColor);
			levelNBT.setInteger("width", level.width);
			levelNBT.setInteger("height", level.height);
			levelNBT.setInteger("depth", level.depth);
			levelNBT.setByteArray("data", level.tiles);

			NBTCompoundIO.write(levelNBT, outputStream);
		} catch (Exception ex) {
			ZeroCraftServer.logger.throwable(ex);
		}
	}

	/**
	 * Decodes the level data from the specified input stream
	 *
	 * @param inputStream the input stream
	 * @return the level data
	 */
	public static byte[] decodeLevelData(InputStream inputStream) {
		try {
			DataInputStream dataInputStream = new DataInputStream(new GZIPInputStream(inputStream));
			byte[] data = new byte[dataInputStream.readInt()];
			dataInputStream.readFully(data);
			dataInputStream.close();
			return data;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static byte[] encodeLevelData(Level level) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] tiles = level.tiles;

		try {
			DataOutputStream dataOutputStream = new DataOutputStream(
					new GZIPOutputStream(outputStream));
			dataOutputStream.writeInt(tiles.length);
			dataOutputStream.write(tiles);
			dataOutputStream.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
		return outputStream.toByteArray();
	}
}