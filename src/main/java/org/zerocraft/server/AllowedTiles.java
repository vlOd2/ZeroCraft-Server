package org.zerocraft.server;

import java.util.ArrayList;

import org.zerocraft.server.level.tile.Tile;

public class AllowedTiles {
	public static final ArrayList<Tile> ALLOWED_TILES = new ArrayList<Tile>();
	
	static {
		ALLOWED_TILES.add(Tile.stone);
		ALLOWED_TILES.add(Tile.wood);
		ALLOWED_TILES.add(Tile.dirt);
		ALLOWED_TILES.add(Tile.cobblestone);
		ALLOWED_TILES.add(Tile.log);
		ALLOWED_TILES.add(Tile.leaf);
		ALLOWED_TILES.add(Tile.bush);
		ALLOWED_TILES.add(Tile.plantYellow);
		ALLOWED_TILES.add(Tile.plantRed);
		ALLOWED_TILES.add(Tile.mushroomBrown);
		ALLOWED_TILES.add(Tile.mushroomRed);
		ALLOWED_TILES.add(Tile.sand);
		ALLOWED_TILES.add(Tile.gravel);
		ALLOWED_TILES.add(Tile.glass);
		ALLOWED_TILES.add(Tile.sponge);
		ALLOWED_TILES.add(Tile.blockGold);
		ALLOWED_TILES.add(Tile.clothRed);
		ALLOWED_TILES.add(Tile.clothOrange);
		ALLOWED_TILES.add(Tile.clothYellow);
		ALLOWED_TILES.add(Tile.clothChartreuse);
		ALLOWED_TILES.add(Tile.clothGreen);
		ALLOWED_TILES.add(Tile.clothSpringGreen);
		ALLOWED_TILES.add(Tile.clothCyan);
		ALLOWED_TILES.add(Tile.clothCapri);
		ALLOWED_TILES.add(Tile.clothUltramarine);
		ALLOWED_TILES.add(Tile.clothViolet);
		ALLOWED_TILES.add(Tile.clothPurple);
		ALLOWED_TILES.add(Tile.clothMagenta);
		ALLOWED_TILES.add(Tile.clothRose);
		ALLOWED_TILES.add(Tile.clothDarkGray);
		ALLOWED_TILES.add(Tile.clothGray);
		ALLOWED_TILES.add(Tile.clothWhite);
	}
}
