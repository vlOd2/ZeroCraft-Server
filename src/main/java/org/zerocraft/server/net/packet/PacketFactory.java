package org.zerocraft.server.net.packet;

import java.util.HashMap;

import org.zerocraft.server.ZeroCraftServer;

public class PacketFactory {
	private static HashMap<Integer, Class<? extends Packet>> packetMap = new HashMap<>();

	static {
		packetMap.put(0, PacketLogin.class);
		packetMap.put(1, PacketPing.class);
		packetMap.put(2, PacketLevelInit.class);
		packetMap.put(3, PacketLevelData.class);
		packetMap.put(4, PacketLevelEnd.class);
		packetMap.put(5, PacketPlaceBreakTile.class);
		packetMap.put(6, PacketUpdateTile.class);
		packetMap.put(7, PacketPlayerJoin.class);
		packetMap.put(8, PacketPlayerTeleport.class);
		packetMap.put(9, PacketPlayerMoveRotate.class);
		packetMap.put(10, PacketPlayerMove.class);
		packetMap.put(11, PacketPlayerRotate.class);
		packetMap.put(12, PacketPlayerLeave.class);
		packetMap.put(13, PacketChat.class);
		packetMap.put(14, PacketLogout.class);
	}

	public static Packet getPacketByID(int id) {
		// Get the packet class by ID
		Class<? extends Packet> packetClass = packetMap.get(id);

		// Check if the specified ID map was successful
		if (packetClass != null) {
			try {
				return packetClass.getDeclaredConstructor().newInstance();
			} catch (Exception ex) {
				// Failure?
				ZeroCraftServer.logger.throwable(ex);
			}
		}

		return null;
	}
}
