package org.zerocraft.server.level.tile;

import java.util.Random;

import org.zerocraft.server.entity.AABB;
import org.zerocraft.server.level.Level;

public class Tile {
	protected static Random random = new Random();
	public static Tile[] tiles = new Tile[256];
	public static boolean[] shouldTick = new boolean[256];
	private static int[] tickSpeed = new int[256];
	public static Tile stone = new Tile(1).setDisplayName("Stone");
	public static Tile grass = new GrassTile(2).setDisplayName("Grass");
	public static Tile dirt = new Tile(3).setDisplayName("Dirt");
	public static Tile cobblestone = new Tile(4).setDisplayName("Cobblestone");
	public static Tile wood = new Tile(5).setDisplayName("Wood");
	public static Tile bush = new SpriteTile(6, 0.25F, 0.9F).setDisplayName("Bush");
	public static Tile bedrock = new Tile(7).setDisplayName("Bedrock");
	public static Tile water = new LiquidTile(8, Liquid.water).setDisplayName("Water");
	public static Tile calmWater = new CalmLiquidTile(9, Liquid.water).setDisplayName("Still Water");
	public static Tile lava = new LiquidTile(10, Liquid.lava).setDisplayName("Lava");
	public static Tile calmLava = new CalmLiquidTile(11, Liquid.lava).setDisplayName("Still Lava");
	public static Tile sand = new FallingTile(12).setDisplayName("Sand");
	public static Tile gravel = new FallingTile(13).setDisplayName("Gravel");
	public static Tile oreGold = new Tile(14).setDisplayName("Gold Ore");
	public static Tile oreIron = new Tile(15).setDisplayName("Iron Ore");
	public static Tile oreCoal = new Tile(16).setDisplayName("Coal Ore");
	public static Tile log = new Tile(17).setDisplayName("Wood Log");
	public static Tile leaf = new Tile(18).setDisplayName("Leaves");
	public static Tile sponge = new SpongeTile(19).setDisplayName("Sponge");
	public static Tile glass = new Tile(20).setDisplayName("Glass");
	public static Tile clothRed = new Tile(21).setDisplayName("Red Wool");
	public static Tile clothOrange = new Tile(22).setDisplayName("Orange Wool");
	public static Tile clothYellow = new Tile(23).setDisplayName("Yellow Wool");
	public static Tile clothChartreuse = new Tile(24).setDisplayName("Chartreuse Wool");
	public static Tile clothGreen = new Tile(25).setDisplayName("Green Wool");
	public static Tile clothSpringGreen = new Tile(26).setDisplayName("Spring Green Wool");
	public static Tile clothCyan = new Tile(27).setDisplayName("Cyan Wool");
	public static Tile clothCapri = new Tile(28).setDisplayName("Capri Wool");
	public static Tile clothUltramarine = new Tile(29).setDisplayName("Ultramarine Wool");
	public static Tile clothViolet = new Tile(30).setDisplayName("Violet Wool");
	public static Tile clothPurple = new Tile(31).setDisplayName("Purple Wool");
	public static Tile clothMagenta = new Tile(32).setDisplayName("Magenta Wool");
	public static Tile clothRose = new Tile(33).setDisplayName("Rose Wool");
	public static Tile clothDarkGray = new Tile(34).setDisplayName("Dark Gray Wool");
	public static Tile clothGray = new Tile(35).setDisplayName("Gray Wool");
	public static Tile clothWhite = new Tile(36).setDisplayName("Wool");
	public static Tile plantYellow = new SpriteTile(37, 0.15F, 0.6F).setDisplayName("Yellow Flower");
	public static Tile plantRed = new SpriteTile(38, 0.15F, 0.6F).setDisplayName("Red Flower");
	public static Tile mushroomBrown = new MushroomTile(39).setDisplayName("Brown Mushroom");
	public static Tile mushroomRed = new MushroomTile(40).setDisplayName("Red Mushroom");
	public static Tile blockGold = new Tile(41).setDisplayName("Gold Block");
	public int id;
	public float xx0;
	public float yy0;
	public float zz0;
	public float xx1;
	public float yy1;
	public float zz1;
	public String displayName;

	protected Tile(int id) {
		tiles[id] = this;
		this.id = id;
		this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	protected Tile setDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	protected void setTicking(boolean value) {
		shouldTick[this.id] = value;
	}

	protected void setShape(float x0, float y0, float z0, float x1, float y1, float z1) {
		this.xx0 = x0;
		this.yy0 = y0;
		this.zz0 = z0;
		this.xx1 = x1;
		this.yy1 = y1;
		this.zz1 = z1;
	}

	public void setTickSpeed(int speed) {
		tickSpeed[this.id] = speed;
	}

	public AABB getAABB(int x, int y, int z) {
		return new AABB(x + this.xx0, y + this.yy0, z + this.zz0, x + this.xx1, y + this.yy1, z + this.zz1);
	}

	public boolean isOpaque() {
		return true;
	}

	public boolean isSolid() {
		return true;
	}

	public void tick(Level level, int x, int y, int z, Random rand) {
	}

	public void destroy(Level level, int x, int y, int z) {
	}

	public Liquid getLiquidType() {
		return Liquid.none;
	}

	public void neighborChanged(Level level, int x, int y, int z, int neighborID) {
	}

	public void onBlockAdded(Level level, int x, int y, int z) {
	}

	public int getTickDelay() {
		return 0;
	}

	public void onPlace(Level level, int x, int y, int z) {
	}

	public void onBreak(Level level, int x, int y, int z) {
	}
}