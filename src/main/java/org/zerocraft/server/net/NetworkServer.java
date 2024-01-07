package org.zerocraft.server.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.zerocraft.server.Utils;
import org.zerocraft.server.ZeroCraftServer;
import org.zerocraft.server.logger.Logger;

public class NetworkServer {
	public static Logger logger = ZeroCraftServer.logger;
	private ServerSocket serverTCPSocket;
	private Thread listenThreadTCP;
	public volatile boolean isListening;
	private int connectionNumber;
	private ArrayList<NetLoginHandler> loginHandlers = new ArrayList<NetLoginHandler>();
	private ArrayList<NetServerHandler> serverHandlers = new ArrayList<NetServerHandler>();

	public NetworkServer(ZeroCraftServer instance, InetAddress address, int port) throws IOException {
		this.serverTCPSocket = new ServerSocket(port, 0, address);
		this.serverTCPSocket.setPerformancePreferences(0, 2, 1);
		this.isListening = true;
		this.listenThreadTCP = new Thread("Network-TCP-Server") {
			@Override
			public void run() {
				while (NetworkServer.this.isListening) {
					try {
						Socket clientConnection = NetworkServer.this.serverTCPSocket.accept();

						if (clientConnection != null) {
							String threadName = "NetConn-" + NetworkServer.this.connectionNumber++;
							NetworkTCPManager netManager = new NetworkTCPManager(clientConnection, threadName, null);
							NetLoginHandler loginHandler = new NetLoginHandler(instance, netManager, threadName);
							NetworkServer.this.addLoginHandler(loginHandler);
						}
						
						Thread.sleep(1);
					} catch (Exception ex) {
						logger.severe("Failed to accept a connection: " + 
								Utils.getThrowableStackTraceAsStr(ex));
					}
				}
			}
		};
		this.listenThreadTCP.start();
	}

	public void addServerHandler(NetServerHandler serverHandler) {
		if (serverHandler == null) {
			throw new IllegalArgumentException("Got NULL for server handler!");
		} else {
			this.serverHandlers.add(serverHandler);
		}
	}

	private void addLoginHandler(NetLoginHandler loginHandler) {
		if (loginHandler == null) {
			throw new IllegalArgumentException("Got NULL for login handler!");
		} else {
			this.loginHandlers.add(loginHandler);
		}
	}

	public void update() throws IOException {
		for (NetLoginHandler loginHandler : this.loginHandlers.toArray(new NetLoginHandler[0])) {
			loginHandler.onUpdate();

			if (loginHandler.connectionClosed) {
				this.loginHandlers.remove(loginHandler);
			}
		}

		for (NetServerHandler serverHandler : this.serverHandlers.toArray(new NetServerHandler[0])) {
			serverHandler.onUpdate();

			if (serverHandler.connectionClosed) {
				this.serverHandlers.remove(serverHandler);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void stop() {
		this.isListening = false;
		this.listenThreadTCP.stop();
		try {
			this.serverTCPSocket.close();
		} catch (Exception ex) {
		}
	}
}
