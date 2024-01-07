package org.zerocraft.server.level.tile;

public class Liquid {
	private static Liquid[] liquids = new Liquid[4];
	public static Liquid none = new Liquid(0);
	public static Liquid water = new Liquid(1);
	public static Liquid lava = new Liquid(2);

	private Liquid(int id) {
		liquids[id] = this;
	}
}