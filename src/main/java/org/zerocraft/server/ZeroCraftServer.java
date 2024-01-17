package org.zerocraft.server;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.zerocraft.server.configuration.BannedConfig;
import org.zerocraft.server.configuration.ConfigLoaderSaver;
import org.zerocraft.server.configuration.MainConfig;
import org.zerocraft.server.configuration.WhitelistConfig;
import org.zerocraft.server.console.Console;
import org.zerocraft.server.consolehandler.ConsoleCaller;
import org.zerocraft.server.consolehandler.ConsoleHandler;
import org.zerocraft.server.level.Level;
import org.zerocraft.server.level.LevelIO;
import org.zerocraft.server.level.tile.Tile;
import org.zerocraft.server.logger.Logger;
import org.zerocraft.server.net.NetServerHandler;
import org.zerocraft.server.net.NetworkAddress;
import org.zerocraft.server.net.NetworkServer;
import org.zerocraft.server.net.packet.Packet;

public class ZeroCraftServer implements Runnable {
	public static final String VERSION_STR = "ZeroCraft Server 0.1";
	public static ZeroCraftServer instance;
	public static Logger logger;
	public volatile boolean running;
	public Console console;
	// TODO: Use CLI scanner
	public Scanner cliScanner;
	public ConsoleCaller consoleCaller = new ConsoleCaller(null);
	public ConsoleHandler consoleHandler;
	public NetworkServer networkServer;
	public final List<NetServerHandler> clients = Collections.synchronizedList(new ArrayList<NetServerHandler>());
	private Object playerIDLookupLock = new Object();
	public Timer timer = new Timer(20.0F);
	public Level level;
	public Level level2;
	public LevelIO levelIO = new LevelIO();

	static {
		FileOutputStream runtimeLogFileStream = null;

		try {
			File logsFolder = new File("logs");
			if (!logsFolder.exists()) {
				logsFolder.mkdir();
			}

			String time = DateTimeFormatter.ofPattern("HH-mm-ss dd.MM.yy").format(LocalDateTime.now());
			File runtimeLogFile = Paths.get("logs", String.format("%s.log", time)).toFile();
			runtimeLogFile.createNewFile();
			runtimeLogFileStream = new FileOutputStream(runtimeLogFile);
		} catch (Exception ex) {
			ex.printStackTrace();
			Runtime.getRuntime().halt(1);
		}
		PrintWriter fileWriter = new PrintWriter(runtimeLogFileStream, true);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				fileWriter.flush();
				fileWriter.close();
			}
		});

		// Logger setup
		logger = new Logger();

		// Console target
		logger.targets.add(new Delegate() {
			@Override
			public void call(Object... args) {
				String str = (String) args[0];
				Color color = (Color) args[1];

				if (instance != null && instance.console != null) {
					instance.console.write(str, color);
				}
			}
		});

		// Stdout and Stderr target
		logger.targets.add(new Delegate() {
			@Override
			public void call(Object... args) {
				String str = (String) args[0];
				Color color = (Color) args[1];

				if (color != Color.red) {
					System.out.println(str);
				} else {
					System.err.println(str);
				}
			}
		});

		// File target
		logger.targets.add(new Delegate() {
			@Override
			public void call(Object... args) {
				String str = (String) args[0];
				fileWriter.println(str);
			}
		});
	}

	public ZeroCraftServer() {
		Thread timerHack = new Thread("Timer-Hack") {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(2147483647L);
					} catch (InterruptedException ex) {
					}
				}
			}
		};
		timerHack.setDaemon(true);
		timerHack.start();

		if (instance != null) {
			throw new RuntimeException("Attempted to re-assign singleton server instance!");
		}
		instance = this;
	}

	public void stop() {
		logger.info("Stopping server...");
		this.disconnectAllClients("Server shutting down!");
		
		this.running = false;
		if (this.level != null) {			
			this.saveLevel(this.level);
			this.saveLevel(this.level2);
		}
		
		if (this.networkServer != null) {
			try {
				this.networkServer.stop();
			} catch (Exception ex) {
			}
		}

		if (this.console != null) {
			this.console.hide();
		}

		System.exit(0);
	}

	public static void showErrorDialog(String body, String title) {
		JOptionPane.showMessageDialog(null, body, title, JOptionPane.ERROR_MESSAGE | JOptionPane.OK_OPTION);
	}

	public static void printErrorHeader(String name) {
		ZeroCraftServer.logger.error("-------------------");
		ZeroCraftServer.logger.error(name);
		ZeroCraftServer.logger.error("-------------------");
	}

	@Override
	public void run() {
		try {
			logger.info("Loading configuration...");
			this.initConfig();

			logger.info("Initializing...");
			logger.info("The server will listen on %s:%d, you can change this in the configuration",
					MainConfig.instance.listenIP, MainConfig.instance.listenPort);

			this.running = true;
			this.networkServer = new NetworkServer(this, InetAddress.getByName(MainConfig.instance.listenIP),
					MainConfig.instance.listenPort);
			if (System.console() != null || System.getProperty("zerocraft.forceConsole") != null) {
				this.cliScanner = new Scanner(System.in);
			}
			this.consoleHandler = new ConsoleHandler(this);
			
			logger.info("Loading main level...");
			this.level = this.loadLevel("Main");
			if (this.level == null) {
				this.level = this.generateLevel("Main", 256, 64, 256);
				this.saveLevel(this.level);
			}
			
			this.level2 = this.loadLevel("Main2");
			if (this.level2 == null) {
				this.level2 = this.generateLevel("Main2", 256, 64, 256);
				this.saveLevel(this.level2);
			}
			
			logger.info("Initialized! Listening on %s:%d...", MainConfig.instance.listenIP,
					MainConfig.instance.listenPort);
		} catch (Exception ex) {
			ZeroCraftServer.printErrorHeader("START-UP FAILED");
			logger.throwable(ex);
			this.running = false;

			if (this.console != null) {
				logger.fatal("You may close this window to quit");
				
				while (true) {
		    		try {
						Thread.sleep(1);
					} catch (Exception ex2) {
						logger.throwable(ex2);
					}
				}
			} else {
				this.stop();
				return;
			}
		}

		Thread thread = new Thread("Server-Game-Tick") {
			@Override
			public void run() {
				while (ZeroCraftServer.this.running) {
					try {
						ZeroCraftServer.this.updateTimer();
						for (int i = 0; i < ZeroCraftServer.this.timer.ticks; i++) {
							ZeroCraftServer.this.tick();
						}
						Thread.sleep(1);
					} catch (Exception ex) {
						printErrorHeader("FATAL ERROR");
						ZeroCraftServer.logger.throwable(ex);
						ZeroCraftServer.this.stop();
						return;
					}
				}
			}
		};
		thread.setDaemon(true);
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
		
		while (this.running) {
			if (this.cliScanner != null) {
				System.out.print("> ");
				String cliInput = this.cliScanner.nextLine();
				this.onConsoleSubmit(cliInput);	
			}
			
    		try {
				Thread.sleep(1);
			} catch (Exception ex) {
				logger.throwable(ex);
			}
		}
	}

	private void updateTimer() {
		Timer timer = this.timer;
		long sysClock = System.currentTimeMillis();
		long sysClockDiff = sysClock - timer.lastSyncSysClock;
		long hrClock = System.nanoTime() / 1000000L;
		double fps;

		if (sysClockDiff > 1000L) {
			long hrClockDiff = hrClock - timer.lastSyncHRClock;
			fps = (double) sysClockDiff / (double) hrClockDiff;
			timer.timeSyncAdjustment += (fps - timer.timeSyncAdjustment) * 0.2F;
			timer.lastSyncSysClock = sysClock;
			timer.lastSyncHRClock = hrClock;
		}

		if (sysClockDiff < 0L) {
			timer.lastSyncSysClock = sysClock;
			timer.lastSyncHRClock = hrClock;
		}

		double hrMicroSecs = hrClock / 1000.0D;
		fps = (hrMicroSecs - timer.lastHRTime) * timer.timeSyncAdjustment;
		timer.lastHRTime = hrMicroSecs;

		if (fps < 0.0D) {
			fps = 0.0D;
		}

		if (fps > 1.0D) {
			fps = 1.0D;
		}

		timer.fps = (float) (timer.fps + fps * timer.timeScale * timer.ticksPerSecond);
		timer.ticks = (int) timer.fps;

		if (timer.ticks > 100) {
			timer.ticks = 100;
		}

		timer.fps -= timer.ticks;
		timer.deltaTime = timer.fps;
	}

	private void tick() throws IOException {
		this.networkServer.update();
		this.level.tickEntities();
		this.level.tick();
	}

	public Level generateLevel(String name, int width, int height, int depth) {
		ZeroCraftServer.logger.info("Generating the level \"%s\"...", name);
		byte[] tiles = new byte[width * height * depth];

		for (int y = 0; y < height; y++) {
			for (int z = 0; z < depth; z++) {
				for (int x = 0; x < width; x++) {
					int tile = 0;

					if (y < 1) {
						tile = Tile.bedrock.id;
					} else if (y < height / 2 - 1) {
						tile = Tile.dirt.id;
					} else if (y < height / 2) {
						tile = Tile.grass.id;
					}

					tiles[(y * depth + z) * width + x] = (byte) tile;
				}
			}
		}

		Level level = new Level();
		level.instance = this;
		level.name = name;
		level.creator = "Anonymous";
		level.createTime = System.currentTimeMillis();
		level.setData(width, height, depth, tiles);
		level.calculateSpawn();
		return level;
	}

	public Level loadLevel(String name) {
		ZeroCraftServer.logger.info("Loading the level \"%s\"...", name);
		
		try {
			File file = new File(String.format("%s.dat", name.toLowerCase().replace(" ", "_")));
			
			if (!file.exists()) {
				ZeroCraftServer.logger.warn("Level \"%s\" doesn't exist", name);
				return null;
			}
			
			Level level = LevelIO.load(new FileInputStream(file));
			level.instance = this;
			return level;
		} catch (Exception ex) {
			ZeroCraftServer.logger.throwable(ex);
			return null;
		}
	}
	
	public void saveLevel(Level level) {
		ZeroCraftServer.logger.info("Saving the level \"%s\"...", level.name);

		try {
			File file = new File(String.format("%s.dat", level.name.toLowerCase().replace(" ", "_")));
			LevelIO.save(level, new FileOutputStream(file));
		} catch (Exception ex) {
			ZeroCraftServer.logger.throwable(ex);
		}
	}

	public void initConfig() {
		this.loadConfig();
		this.saveConfig();
	}
	
	public void loadConfig() {
		ConfigLoaderSaver cls = new ConfigLoaderSaver(MainConfig.instance = new MainConfig(), 
				new File("main.yml"));
		cls.load();
		
		cls = new ConfigLoaderSaver(WhitelistConfig.instance = new WhitelistConfig(), 
				new File("whitelist.yml"));
		cls.load();
		
		cls = new ConfigLoaderSaver(BannedConfig.instance = new BannedConfig(), 
				new File("banned.yml"));
		cls.load();
	}
	
	public void saveConfig() {
		ConfigLoaderSaver cls = new ConfigLoaderSaver(MainConfig.instance, new File("main.yml"));
		cls.save();
		
		cls = new ConfigLoaderSaver(WhitelistConfig.instance, new File("whitelist.yml"));
		cls.save();
		
		cls = new ConfigLoaderSaver(BannedConfig.instance, new File("banned.yml"));
		cls.save();
	}
	
	public void banPlayer(String target, String reason, boolean ip) {
		if (ip) {
			BannedConfig.instance.ips.put(target, reason);
			logger.log("Moderation", "Banned the IP \"" + target + "\" for \"" + reason + "\"");
			
			NetServerHandler[] handlers = this.getHandlersByAddress(target);
			for (NetServerHandler handler : handlers) {
				handler.kick("You have been IP banned: " + reason);
			}
		} else {
			BannedConfig.instance.users.put(target, reason);
			logger.log("Moderation", "Banned the user \"" + target + "\" for \"" + reason + "\"");
			
			NetServerHandler handler = this.getHandlerByName(target);
			if (handler != null) {
				handler.kick("You have been banned: " + reason);
			}
		}
		
		this.saveConfig();
	}
	
	public void unbanPlayer(String target, boolean ip) {
		if (ip) {
			BannedConfig.instance.ips.remove(target);
			logger.log("Moderation", "Unbanned the IP \"" + target + "\"");
		} else {
			BannedConfig.instance.users.remove(target);
			logger.log("Moderation", "Unbanned the user \"" + target + "\"");
		}
		this.saveConfig();
	}
	
	public byte getNewPlayerID() {
		synchronized (this.playerIDLookupLock) {			
			for (byte id = 0; id < 127; id++) {
				if (this.getHandlerByPlayerID(id) == null) {
					return id;
				}
			}
			
			return -1;
		}
	}
	
	public NetServerHandler getHandlerByPlayerID(byte id) {
		if (id > 127) {
			throw new IndexOutOfBoundsException("Out-of-bounds player ID!");
		}
		
		for (NetServerHandler handler : this.clients.toArray(new NetServerHandler[0])) {
			if (handler.playerEntity != null && handler.playerEntity.playerID == id) {
				return handler;
			}
		}

		return null;
	}
	
	public NetServerHandler getHandlerByName(String name) {
		for (NetServerHandler handler : this.clients.toArray(new NetServerHandler[0])) {
			if (handler.userName != null && handler.userName.equals(name)) {
				return handler;
			}
		}

		return null;
	}

	public NetServerHandler[] getHandlersByAddress(String address) {
		ArrayList<NetServerHandler> handlers = new ArrayList<NetServerHandler>();

		for (NetServerHandler handler : this.clients.toArray(new NetServerHandler[0])) {
			// Using String.equals instead of == to compensate for any weird cases
			if (handler.netManager.getAddress().ip.equals(address)) {
				handlers.add(handler);
			}
		}

		return handlers.toArray(new NetServerHandler[0]);
	}

	public NetServerHandler getHandlerByAddress(NetworkAddress address) {
		for (NetServerHandler handler : this.clients.toArray(new NetServerHandler[0])) {
			// Using String.equals instead of == to compensate for any weird cases
			if (handler.netManager.getAddress().ip.equals(address.ip)
					&& handler.netManager.getAddress().port == address.port) {
				return handler;
			}
		}

		return null;
	}

	public void sendGlobalChatMessage(String message) {
		for (NetServerHandler handler : this.clients.toArray(new NetServerHandler[0])) {
			handler.sendChatMessage(message);
		}
	}

	public void sendLevelPacket(Packet packet, Level level, NetServerHandler... exclusionList) {
		List<NetServerHandler> exclusionListAsList = Arrays.asList(exclusionList);
		for (NetServerHandler handler : this.clients.toArray(new NetServerHandler[0])) {
			if (exclusionListAsList.contains(handler)) {
				continue;
			}
			
			if (handler.currentLevel != level) {				
				continue;
			}
			
			handler.sendPacket(packet);
		}
	}
	
	public void sendGlobalPacket(Packet packet) {
		for (NetServerHandler handler : this.clients.toArray(new NetServerHandler[0])) {
			handler.sendPacket(packet);
		}
	}

	public void disconnectAllClients(String reason) {
		for (NetServerHandler handler : this.clients.toArray(new NetServerHandler[0])) {
			handler.kick(reason);
		}
	}

	public void onConsoleSubmit(String cmd) {
		this.consoleHandler.handleInput(cmd, this.consoleCaller);
	}

	public static void main(String[] args) {
		ZeroCraftServer instance = new ZeroCraftServer();

		if (!GraphicsEnvironment.isHeadless() && System.getProperty("zerocraft.noGUI") == null) {
			instance.console = new Console();
			instance.console.onSubmit = new Delegate() {
				@Override
				public void call(Object... args) {
					instance.onConsoleSubmit((String) args[0]);
				}
			};
			instance.console.onClose = new Delegate() {
				@Override
				public void call(Object... args) {
					instance.stop();
				}
			};
			instance.console.show();
		}
		
		Thread thread = new Thread(instance, "Server");
		thread.start();
	}
}