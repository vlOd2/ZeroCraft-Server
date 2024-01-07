package org.zerocraft.server.net;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.zerocraft.server.ZeroCraftServer;
import org.zerocraft.server.net.packet.Packet;
import org.zerocraft.server.net.packet.PacketFactory;

public class NetworkTCPManager implements NetworkManager {
	private Socket socket;
	private NetworkAddress address;
	private ClassicDataInputStream inputStream;
	private ClassicDataOutputStream outputStream;
	private boolean connected;
	private boolean isTerminating;
	private boolean isClosing;
	private String terminationReason;
	private Object sendQueueLock = new Object();
	private int sendQueueByteLength;
	private List<Packet> readPackets = Collections.synchronizedList(new LinkedList<>());
	private List<Packet> sendPackets = Collections.synchronizedList(new LinkedList<>());
	private Thread writeThread;
	private Thread readThread;
	private NetBaseHandler netHandler;
	private int timeSinceLastRead;

	public NetworkTCPManager(Socket socket, String threadName, NetBaseHandler netHandler) throws IOException {
		this.socket = socket;
		this.netHandler = netHandler;
		socket.setTrafficClass(24); // IPTOS_THROUGHPUT + IPTOS_LOWDELAY
		this.inputStream = new ClassicDataInputStream(socket.getInputStream());
		this.outputStream = new ClassicDataOutputStream(new BufferedOutputStream(socket.getOutputStream(), 5120));
		this.address = new NetworkAddress(socket);
		this.connected = true;

		this.readThread = new Thread(threadName + "-Reader") {
			@Override
			public void run() {
				while (true) {
					if (!NetworkTCPManager.this.connected || NetworkTCPManager.this.isClosing) {
						break;
					}
					NetworkTCPManager.this.readPacket();
				}
			}
		};

		this.writeThread = new Thread(threadName + "-Writer") {
			@Override
			public void run() {
				while (true) {
					if (!NetworkTCPManager.this.connected) {
						break;
					}
					NetworkTCPManager.this.sendPacket();
				}
			}
		};
		this.readThread.start();
		this.writeThread.start();
	}

	@Override
	public void setNetHandler(NetBaseHandler netHandler) {
		this.netHandler = netHandler;
	}

	@Override
	public void addToQueue(Packet packet) {
		if (this.isClosing) {
			return;
		}

		synchronized (this.sendQueueLock) {
			this.sendQueueByteLength += 1 + packet.getPacketSize();
			this.sendPackets.add(packet);
		}
	}

	private void sendPacket() {
		try {
			if (this.sendPackets.isEmpty()) {
				Thread.sleep(10);
				return;
			}

			Packet packet;
			synchronized (this.sendQueueLock) {
				packet = this.sendPackets.remove(0);
				this.sendQueueByteLength -= 1 + packet.getPacketSize();
			}

			// Write the packet
			this.outputStream.write(packet.getID());
			packet.write(this.outputStream);
			this.outputStream.flush();
		} catch (InterruptedException ex) {
		} catch (Exception ex) {
			if (this.isTerminating || this.isClosing) {
				return;
			}
			this.handleNetworkError(ex);
		}
	}

	private void readPacket() {
		try {
			int packetID = this.inputStream.read();
			if (packetID == -1) {
				this.shutdown("Client disconnect");
				return;
			}

			Packet packet = PacketFactory.getPacketByID(packetID);
			if (packet == null) {
				this.shutdown("Bad packet ID " + packetID);
				return;
			}

			packet.read(this.inputStream);
			this.readPackets.add(packet);
		} catch (Exception ex) {
			if (this.isTerminating || this.isClosing) {
				return;
			}
			
			if (ex instanceof SocketException) {
				this.shutdown("Client disconnect");
				return;
			}
			
			this.handleNetworkError(ex);
		}
	}

	private void handleNetworkError(Exception ex) {
		ZeroCraftServer.logger.throwable(ex);
		this.shutdown(ex.toString());
	}

	@Override
	public void shutdown(String reason) {
		if (!this.connected) {
			return;
		}
		this.isTerminating = true;
		this.terminationReason = reason;

		new Thread("Network-Terminator") {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
					if (NetworkTCPManager.this.readThread.isAlive()) {
						try {
							NetworkTCPManager.this.readThread.stop();
						} catch (Exception ex) {
						}
					}

					if (NetworkTCPManager.this.writeThread.isAlive()) {
						try {
							NetworkTCPManager.this.writeThread.stop();
						} catch (Exception ex) {
						}
					}
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}.start();
		this.connected = false;

		try {
			this.inputStream.close();
		} catch (Exception ex) {
		}

		try {
			this.outputStream.close();
		} catch (Exception ex) {
		}

		try {
			this.socket.close();
		} catch (Exception ex) {
		}
	}

	@Override
	public void processReceivedPackets() throws IOException {
		if (this.sendQueueByteLength > 0x100000) { // A megabyte
			this.shutdown("Send buffer overflow");
		}

		if (this.readPackets.isEmpty()) {
			if (this.timeSinceLastRead++ == 200) { // 10 seconds
				this.shutdown("No packet read within 10 seconds");
			}
		} else {
			this.timeSinceLastRead = 0;
		}
		
		int packetsLimit = 100;
		while (!this.readPackets.isEmpty() && packetsLimit-- >= 0) {
			Packet packet = this.readPackets.remove(0);
			this.netHandler.handlePacket(packet);
		}

		if (this.isTerminating && this.readPackets.isEmpty()) {
			this.netHandler.handleTermination(this.terminationReason);
		}
	}

	@Override
	public InetSocketAddress getSocketAddress() {
		return (InetSocketAddress) this.socket.getRemoteSocketAddress();
	}

	@Override
	public NetworkAddress getAddress() {
		return this.address;
	}

	@Override
	public ClassicDataInputStream getInputStream() {
		return this.inputStream;
	}

	@Override
	public ClassicDataOutputStream getOutputStream() {
		return this.outputStream;
	}

	@Override
	public void interrupt() {
		this.readThread.interrupt();
		this.writeThread.interrupt();
	}

	@Override
	public void close() {
		this.isClosing = true;
		this.readThread.interrupt();

		new Thread("Network-Closer") {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
					if (NetworkTCPManager.this.connected) {
						NetworkTCPManager.this.writeThread.interrupt();
						NetworkTCPManager.this.shutdown("Connection closed");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.start();
	}
}
