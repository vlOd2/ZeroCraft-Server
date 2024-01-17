package org.zerocraft.server.net;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.zerocraft.server.AllowedInputCharacters;
import org.zerocraft.server.AllowedTiles;
import org.zerocraft.server.ChatColor;
import org.zerocraft.server.Utils;
import org.zerocraft.server.ZeroCraftServer;
import org.zerocraft.server.consolehandler.ConsoleCaller;
import org.zerocraft.server.consolehandler.ConsoleHandler;
import org.zerocraft.server.entity.Player;
import org.zerocraft.server.level.Level;
import org.zerocraft.server.level.LevelIO;
import org.zerocraft.server.level.tile.Tile;
import org.zerocraft.server.net.packet.Packet;
import org.zerocraft.server.net.packet.PacketChat;
import org.zerocraft.server.net.packet.PacketLevelData;
import org.zerocraft.server.net.packet.PacketLevelEnd;
import org.zerocraft.server.net.packet.PacketLevelInit;
import org.zerocraft.server.net.packet.PacketLogin;
import org.zerocraft.server.net.packet.PacketPing;
import org.zerocraft.server.net.packet.PacketPlaceBreakTile;
import org.zerocraft.server.net.packet.PacketPlayerJoin;
import org.zerocraft.server.net.packet.PacketPlayerLeave;
import org.zerocraft.server.net.packet.PacketPlayerTeleport;
import org.zerocraft.server.net.packet.PacketUpdateTile;

public class NetServerHandler extends NetBaseHandler {
	private ZeroCraftServer instance;
	public Level currentLevel;
	public Player playerEntity;
	private ConsoleHandler consoleHandler;
	private int pingTicks;
	private boolean teleported;

	public NetServerHandler(ZeroCraftServer instance, NetworkManager netManager, String userName) {
		this.instance = instance;
		this.netManager = netManager;
		this.userName = userName;
		this.consoleHandler = new ConsoleHandler(instance);
		this.netManager.setNetHandler(this);
		this.instance.clients.add(this);
		this.logger.info("%s logged in", this.userName);
		this.instance.sendGlobalChatMessage(String.format("&8(&a+&8)&d %s", this.userName));
		this.changeLevel(this.instance.level);
	}

	@Override
	public void onUpdate() throws IOException {
		if (this.connectionClosed) {
			return;
		}

		this.netManager.processReceivedPackets();
		this.netManager.interrupt();

		this.pingTicks++;
		if (this.pingTicks % 10 == 0) {
			this.sendPacket(new PacketPing());
		}
	}

	@Override
	public void handlePacket(Packet packet) {
		try {
			if (!(packet instanceof PacketPlayerTeleport)) {
				logger.verbose("Received %s from %s", packet, this);
			}
			String packetName = packet.getClass().getSimpleName().replace("Packet", "");
			String methodName = String.format("handle%sPacket", packetName);
			Method handler = this.getClass().getMethod(methodName, packet.getClass());
			handler.invoke(this, packet);
		} catch (InvocationTargetException ex) {
			logger.error("The packet handler for %s has encountered an error: %s", this,
					Utils.getThrowableStackTraceAsStr(ex.getCause()));
			this.netManager.shutdown("Internal Server Error");
		} catch (Exception ex) {
			this.onBadPacket();
		}
	}

	@Override
	protected void onDisconnect() {
		if (this.currentLevel != null) {
			this.currentLevel.entities.remove(this.playerEntity);
			this.sendPacketToOthers(new PacketPlayerLeave(this.playerEntity.playerID));
		}
		this.instance.sendGlobalChatMessage(String.format("&8(&c-&8)&d %s", this.userName));
		this.instance.clients.remove(this);
	}

	private void sendPacketToOthers(Packet packet) {
		this.instance.sendLevelPacket(packet, this.currentLevel, this);
	}

	private void sendLevelData(Level level) {
		this.sendPacket(new PacketLevelInit());
		byte[] data = LevelIO.encodeLevelData(level);
		List<byte[]> chunks = Utils.splitBytesIntoChunks(data, 1024);

		for (byte[] chunk : chunks) {
			this.sendPacket(new PacketLevelData((short) chunk.length, chunk, 0));
		}

		this.sendPacket(new PacketLevelEnd((short) level.width, (short) level.height, (short) level.depth));
	}

	public void changeLevel(Level level) {
		if (level == null) {
			throw new IllegalArgumentException("Level cannot be null!");
		}

		if (this.playerEntity == null) {
			byte playerID = this.instance.getNewPlayerID();
			if (playerID < 0) {
				this.kick("Reached the maximum players supported by the protocol!");
				return;
			}
			this.playerEntity = new Player(level, this, playerID);
		}

		if (this.currentLevel != null) {
			this.currentLevel.entities.remove(this.playerEntity);
			this.sendPacketToOthers(new PacketPlayerLeave(this.playerEntity.playerID));
			this.instance.sendGlobalChatMessage(
					String.format("&8(&d!&8)&e %s &achanged level&7: &c%s &8-> &b%s", this.userName, this.currentLevel.name, level.name));
			this.logger.info("%s is changing the level from \"%s\" to \"%s\"", this, this.currentLevel.name,
					level.name);
		}

		this.currentLevel = level;
		this.playerEntity.changeLevel(this.currentLevel);
		this.currentLevel.entities.add(this.playerEntity);
		this.sendPacket(new PacketLogin(PROTOCOL_VERSION, "ZeroCraftServer", 
				String.format("Loading the level \"%s\"", this.currentLevel.name), 0));
		this.sendLevelData(this.currentLevel);

		short x = (short) (this.playerEntity.x * 32.0F);
		short y = (short) (this.playerEntity.y * 32.0F);
		short z = (short) (this.playerEntity.z * 32.0F);
		int yaw = (int) this.playerEntity.yaw;
		int pitch = (int) this.playerEntity.pitch;
		this.sendPacketToOthers(new PacketPlayerJoin(this.playerEntity.playerID, this.userName, x, y, z, yaw, pitch));
		this.sendPacket(new PacketPlayerJoin((byte) -1, this.userName, x, y, z, yaw, pitch));

		for (NetServerHandler handler : this.instance.clients.toArray(new NetServerHandler[0])) {
			if (handler == this || handler.currentLevel != level) {
				continue;
			}

			Player player = handler.playerEntity;
			x = (short) (player.x * 32.0F);
			y = (short) (player.y * 32.0F);
			z = (short) (player.z * 32.0F);
			yaw = (int) player.yaw;
			pitch = (int) player.pitch;
			this.sendPacket(new PacketPlayerJoin((byte) player.playerID, handler.userName, x, y, z, yaw, pitch));
		}
	}

	public void teleport(float x, float y, float z, float yaw, float pitch) {
		this.playerEntity.setPosRot(x, y, z, yaw, pitch);
		this.syncPosition(false);
	}

	public void syncPosition(boolean onlyOthers) {
		short x = (short) (this.playerEntity.x * 32.0F);
		short y = (short) (this.playerEntity.y * 32.0F);
		short z = (short) (this.playerEntity.z * 32.0F);
		int yaw = (int) this.playerEntity.yaw;
		int pitch = (int) this.playerEntity.pitch;
		if (!onlyOthers) {
			this.sendPacket(new PacketPlayerTeleport((byte) -1, x, y, z, yaw, pitch));
			this.teleported = true;
		}
		this.sendPacketToOthers(new PacketPlayerTeleport((byte) this.playerEntity.playerID, x, y, z, yaw, pitch));
	}

	public void handlePlayerTeleportPacket(PacketPlayerTeleport packet) {
		if (this.teleported) {
			this.teleported = false;
			return;
		}
		float x = packet.xPos / 32.0F;
		float y = packet.yPos / 32.0F;
		float z = packet.zPos / 32.0F;
		float yaw = packet.yaw;
		float pitch = packet.pitch;
		this.playerEntity.setPosRot(x, y, z, yaw, pitch);
		this.syncPosition(true);
	}

	public void handlePlaceBreakTilePacket(PacketPlaceBreakTile packet) {
		short x = packet.xPos;
		short y = packet.yPos;
		short z = packet.zPos;
		int mode = packet.mode;
		int tileID = packet.type;
		int existingTileID = this.currentLevel.getTile(x, y, z);

		if (tileID < 0 || tileID > 256 || (tileID > 0 && Tile.tiles[tileID] == null)) {
			this.kick("Illegal tile ID!");
			return;
		}

		if (tileID == 0 && mode == 1) {
			this.kick("Bad place/break mode!");
			return;
		}

		Tile tile = Tile.tiles[tileID];
		Tile existingTile = Tile.tiles[existingTileID];

		this.sendPacket(new PacketUpdateTile(x, y, z, (byte) existingTileID));
		if (tile != null && !AllowedTiles.ALLOWED_TILES.contains(tile)) {
			this.sendChatMessage("&8(&d!&8)&c You can't use that tile!");
			return;
		}
		
		if (mode == 1) {
			if (this.currentLevel.setTile(x, y, z, tileID) && tile != null) {
				tile.onBlockAdded(this.currentLevel, x, y, z);
			}
		} else {
			if (this.currentLevel.setTile(x, y, z, 0) && existingTile != null) {
				existingTile.destroy(this.currentLevel, x, y, z);
			}
		}
	}

	public void sendChatMessage(String message) {
		for (String chunk : Utils.splitStringIntoChunks(message, 64)) {
			this.sendPacket(new PacketChat((byte) 0, ChatColor.sanitizeColorCodes(chunk)));
		}
	}
	
	/**
	 * Sends a message to the global chat that came from this player
	 * 
	 * @param msg the message to sent
	 */
	public void sentMessage(String msg) {
		this.logger.info("%s said: %s", this, msg);
		this.instance.sendGlobalChatMessage(ChatColor.format("&d%s&7:&f %s", this.userName, msg));
	}
	
	public void handleChatPacket(PacketChat packet) {
		String msg = packet.message.trim();

		if (ChatColor.stripColorCodes(msg, '%').length() < 1) {
			this.sendChatMessage("&8(&d!&8)&c You may not send just color codes!");
			return;
		}

		for (char c : msg.toCharArray()) {
			if (AllowedInputCharacters.ALLOWED_CHARS.indexOf(c) < 0) {
				this.kick("Illegal chat message!");
				return;
			}
		}

		if (msg.startsWith("/")) {
			msg = msg.replaceFirst("\\/", "");
			this.logger.info("%s executed the comamnd: %s", this, msg);
			this.consoleHandler.handleInput(msg, new ConsoleCaller(this));
			return;
		}
		
		this.sentMessage(msg);
	}
}
