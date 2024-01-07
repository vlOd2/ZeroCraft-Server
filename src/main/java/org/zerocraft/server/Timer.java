package org.zerocraft.server;

public class Timer {
	public float ticksPerSecond;
	public double lastHRTime;
	public int ticks;
	public float deltaTime;
	public float timeScale = 1.0F;
	public float fps;
	public long lastSyncSysClock;
	public long lastSyncHRClock;
	double timeSyncAdjustment = 1.0D;

	public Timer(float ticksPerSecond) {
		this.ticksPerSecond = ticksPerSecond;
		this.lastSyncSysClock = System.currentTimeMillis();
		this.lastSyncHRClock = System.nanoTime() / 1000000L;
	}
}