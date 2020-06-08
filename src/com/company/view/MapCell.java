package com.company.view;

public enum MapCell {
	TANK_1_LVL_1_STATE_1_UP(0, 0, 32),
	TANK_1_LVL_1_STATE_2_UP(34, 0, 32),
	TANK_1_LVL_2_STATE_1_UP(68, 0, 32),
	TANK_1_LVL_2_STATE_2_UP(102, 0, 32),
	TANK_1_LVL_3_STATE_1_UP(136, 0, 32),
	TANK_1_LVL_3_STATE_2_UP(170, 0, 32),
	TANK_1_LVL_4_STATE_1_UP(204, 0, 32),
	TANK_1_LVL_4_STATE_2_UP(238, 0, 32),
	TANK_1_LVL_1_STATE_1_RIGHT(0, 34, 32),
	TANK_1_LVL_1_STATE_2_RIGHT(34, 34, 32),
	TANK_1_LVL_2_STATE_1_RIGHT(68, 34, 32),
	TANK_1_LVL_2_STATE_2_RIGHT(102, 34, 32),
	TANK_1_LVL_3_STATE_1_RIGHT(136, 34, 32),
	TANK_1_LVL_3_STATE_2_RIGHT(170, 34, 32),
	TANK_1_LVL_4_STATE_1_RIGHT(204, 34, 32),
	TANK_1_LVL_4_STATE_2_RIGHT(238, 34, 32),
	TANK_1_LVL_1_STATE_1_DOWN(0, 68, 32),
	TANK_1_LVL_1_STATE_2_DOWN(34, 68, 32),
	TANK_1_LVL_2_STATE_1_DOWN(68, 68, 32),
	TANK_1_LVL_2_STATE_2_DOWN(102, 68, 32),
	TANK_1_LVL_3_STATE_1_DOWN(136, 68, 32),
	TANK_1_LVL_3_STATE_2_DOWN(170, 68, 32),
	TANK_1_LVL_4_STATE_1_DOWN(204, 68, 32),
	TANK_1_LVL_4_STATE_2_DOWN(238, 68, 32),
	TANK_1_LVL_1_STATE_1_LEFT(0, 102, 32),
	TANK_1_LVL_1_STATE_2_LEFT(34, 102, 32),
	TANK_1_LVL_2_STATE_1_LEFT(68, 102, 32),
	TANK_1_LVL_2_STATE_2_LEFT(102, 102, 32),
	TANK_1_LVL_3_STATE_1_LEFT(136, 102, 32),
	TANK_1_LVL_3_STATE_2_LEFT(170, 102, 32),
	TANK_1_LVL_4_STATE_1_LEFT(204, 102, 32),
	TANK_1_LVL_4_STATE_2_LEFT(238, 102, 32),
	// Second tank (green):
	TANK_2_LVL_1_STATE_1_UP(272, 0, 32),
	TANK_2_LVL_1_STATE_2_UP(306, 0, 32),
	TANK_2_LVL_2_STATE_1_UP(340, 0, 32),
	TANK_2_LVL_2_STATE_2_UP(374, 0, 32),
	TANK_2_LVL_3_STATE_1_UP(408, 0, 32),
	TANK_2_LVL_3_STATE_2_UP(442, 0, 32),
	TANK_2_LVL_4_STATE_1_UP(476, 0, 32),
	TANK_2_LVL_4_STATE_2_UP(510, 0, 32),
	TANK_2_LVL_1_STATE_1_RIGHT(272, 34, 32),
	TANK_2_LVL_1_STATE_2_RIGHT(306, 34, 32),
	TANK_2_LVL_2_STATE_1_RIGHT(340, 34, 32),
	TANK_2_LVL_2_STATE_2_RIGHT(374, 34, 32),
	TANK_2_LVL_3_STATE_1_RIGHT(408, 34, 32),
	TANK_2_LVL_3_STATE_2_RIGHT(442, 34, 32),
	TANK_2_LVL_4_STATE_1_RIGHT(476, 34, 32),
	TANK_2_LVL_4_STATE_2_RIGHT(510, 34, 32),
	TANK_2_LVL_1_STATE_1_DOWN(272, 68, 32),
	TANK_2_LVL_1_STATE_2_DOWN(306, 68, 32),
	TANK_2_LVL_2_STATE_1_DOWN(340, 68, 32),
	TANK_2_LVL_2_STATE_2_DOWN(374, 68, 32),
	TANK_2_LVL_3_STATE_1_DOWN(408, 68, 32),
	TANK_2_LVL_3_STATE_2_DOWN(442, 68, 32),
	TANK_2_LVL_4_STATE_1_DOWN(476, 68, 32),
	TANK_2_LVL_4_STATE_2_DOWN(510, 68, 32),
	TANK_2_LVL_1_STATE_1_LEFT(272, 102, 32),
	TANK_2_LVL_1_STATE_2_LEFT(306, 102, 32),
	TANK_2_LVL_2_STATE_1_LEFT(340, 102, 32),
	TANK_2_LVL_2_STATE_2_LEFT(374, 102, 32),
	TANK_2_LVL_3_STATE_1_LEFT(408, 102, 32),
	TANK_2_LVL_3_STATE_2_LEFT(442, 102, 32),
	TANK_2_LVL_4_STATE_1_LEFT(476, 102, 32),
	TANK_2_LVL_4_STATE_2_LEFT(510, 102, 32),

	EAGLE(0, 136, 32),
	EAGLE_DESTROYED(34, 136, 32, false),

	STEEL(0, 442, 16, false),
	BRICK(17, 442, 16),
	BRICK_L_UP_LEFT(34, 442, 16),
	BRICK_L_UP_RIGHT(51, 442, 16),
	BRICK_L_DOWN_RIGHT(68, 442, 16),
	BRICK_L_DOWN_LEFT(85, 442, 16),
	BRICK_I_LEFT(102, 442, 16),
	BRICK_I_DOWN(119, 442, 16),
	BRICK_I_RIGHT(136, 442, 16),
	BRICK_I_UP(153, 442, 16),
	BRICK_UP_LEFT(170, 442, 16),
	BRICK_UP_RIGHT(187, 442, 16),
	BRICK_DOWN_RIGHT(204, 442, 16),
	BRICK_DOWN_LEFT(221, 442, 16),
	ICE(544, 68, 32, false, true),
	FOREST(544, 102, 32, false, true),
	WATER(544, 136, 32, false, false),
	NULL_BLOCKADE(578, 136, 32, false, false),
	NULL_UNIT_BLOCKADE(578, 118, 16, false, false),

	TIMER(68, 136, 32, false, true),
	BOMB(102, 136, 32, false, true),
	STAR(136, 136, 32, true),
	TANK_LIVE(170, 136, 32, false, true),
	HELMET(204, 136, 32, false, true),
	SPADE(238, 136, 32, false, true),
	IMMORTALITY_1(476, 136, 32, false, true),
	IMMORTALITY_2(510, 136, 32, false, true),

	CREATE_1(272, 136, 32, false),
	CREATE_2(306, 136, 32, false),
	CREATE_3(340, 136, 32, false),
	CREATE_4(374, 136, 32, false),
	CREATE_5(408, 136, 32, false),
	CREATE_6(442, 136, 32, false),

	BULLET_UP(306, 442, 8),
	BULLET_RIGHT(314, 442, 8),
	BULLET_LEFT(306, 450, 8),
	BULLET_DOWN(314, 450, 8),
	EXPLODE_1(408, 306, 32, false),
	EXPLODE_2(442, 306, 32, false),
	EXPLODE_3(476, 306, 32, false),
	EXPLODE_4(510, 306, 32, false),
	EXPLODE_5(544, 306, 32, false);

	private int imageCol, imageRow;
	private int width;
	private boolean accessible, destructible;

	MapCell(int col, int row, int size){
		imageCol = col;
		imageRow = row;
		width = size;
		destructible = true;
		accessible = false;
	}
	MapCell(int col, int row, int size, boolean isDestructible){
		imageCol = col;
		imageRow = row;
		width = size;
		destructible = isDestructible;
		accessible = false;
	}
	MapCell(int col, int row, int size, boolean isDestructible, boolean accessibility){
		imageCol = col;
		imageRow = row;
		width = size;
		destructible = isDestructible;
		accessible = accessibility;
	}

	public int getRow(){
		return imageCol;
	}
	public int getCol(){
		return imageRow;
	}

	public int getSize(){
		return width;
	}
	public static int getUnitSize(){
		return 16;
	}

	public boolean isAccessible(){
		return accessible;
	}
	public boolean isDestructible(){
		return destructible;
	}

	// - - - - - Icons for 1-st player for several side of movements;
	public static MapCell[] player1UpState(int level){
		switch(level){
			case 2:
				return new MapCell[]{MapCell.TANK_1_LVL_2_STATE_1_UP, MapCell.TANK_1_LVL_2_STATE_2_UP};
			case 3:
				return new MapCell[]{MapCell.TANK_1_LVL_3_STATE_1_UP, MapCell.TANK_1_LVL_3_STATE_2_UP};
			case 4:
				return new MapCell[]{MapCell.TANK_1_LVL_4_STATE_1_UP, MapCell.TANK_1_LVL_4_STATE_2_UP};
			default:
				return new MapCell[]{MapCell.TANK_1_LVL_1_STATE_1_UP, MapCell.TANK_1_LVL_1_STATE_2_UP};
		}
	}
	public static MapCell[] player1RightState(int level){
		switch(level){
			case 2:
				return new MapCell[]{MapCell.TANK_1_LVL_2_STATE_1_RIGHT, MapCell.TANK_1_LVL_2_STATE_2_RIGHT};
			case 3:
				return new MapCell[]{MapCell.TANK_1_LVL_3_STATE_1_RIGHT, MapCell.TANK_1_LVL_3_STATE_2_RIGHT};
			case 4:
				return new MapCell[]{MapCell.TANK_1_LVL_4_STATE_1_RIGHT, MapCell.TANK_1_LVL_4_STATE_2_RIGHT};
			default:
				return new MapCell[]{MapCell.TANK_1_LVL_1_STATE_1_RIGHT, MapCell.TANK_1_LVL_1_STATE_2_RIGHT};
		}
	}
	public static MapCell[] player1DownState(int level){
		switch(level){
			case 2:
				return new MapCell[]{MapCell.TANK_1_LVL_2_STATE_1_DOWN, MapCell.TANK_1_LVL_2_STATE_2_DOWN};
			case 3:
				return new MapCell[]{MapCell.TANK_1_LVL_3_STATE_1_DOWN, MapCell.TANK_1_LVL_3_STATE_2_DOWN};
			case 4:
				return new MapCell[]{MapCell.TANK_1_LVL_4_STATE_1_DOWN, MapCell.TANK_1_LVL_4_STATE_2_DOWN};
			default:
				return new MapCell[]{MapCell.TANK_1_LVL_1_STATE_1_DOWN, MapCell.TANK_1_LVL_1_STATE_2_DOWN};
		}
	}
	public static MapCell[] player1LeftState(int level){
		switch(level){
			case 2:
				return new MapCell[]{MapCell.TANK_1_LVL_2_STATE_1_LEFT, MapCell.TANK_1_LVL_2_STATE_2_LEFT};
			case 3:
				return new MapCell[]{MapCell.TANK_1_LVL_3_STATE_1_LEFT, MapCell.TANK_1_LVL_3_STATE_2_LEFT};
			case 4:
				return new MapCell[]{MapCell.TANK_1_LVL_4_STATE_1_LEFT, MapCell.TANK_1_LVL_4_STATE_2_LEFT};
			default:
				return new MapCell[]{MapCell.TANK_1_LVL_1_STATE_1_LEFT, MapCell.TANK_1_LVL_1_STATE_2_LEFT};
		}
	}

	// - - - - - Icons for 2-nd player for several side of movements;
	public static MapCell[] player2UpState(int level){
		switch(level){
			case 2:
				return new MapCell[]{MapCell.TANK_2_LVL_2_STATE_1_UP, MapCell.TANK_2_LVL_2_STATE_2_UP};
			case 3:
				return new MapCell[]{MapCell.TANK_2_LVL_3_STATE_1_UP, MapCell.TANK_2_LVL_3_STATE_2_UP};
			case 4:
				return new MapCell[]{MapCell.TANK_2_LVL_4_STATE_1_UP, MapCell.TANK_2_LVL_4_STATE_2_UP};
			default:
				return new MapCell[]{MapCell.TANK_2_LVL_1_STATE_1_UP, MapCell.TANK_2_LVL_1_STATE_2_UP};
		}
	}
	public static MapCell[] player2RightState(int level){
		switch(level){
			case 2:
				return new MapCell[]{MapCell.TANK_2_LVL_2_STATE_1_RIGHT, MapCell.TANK_2_LVL_2_STATE_2_RIGHT};
			case 3:
				return new MapCell[]{MapCell.TANK_2_LVL_3_STATE_1_RIGHT, MapCell.TANK_2_LVL_3_STATE_2_RIGHT};
			case 4:
				return new MapCell[]{MapCell.TANK_2_LVL_4_STATE_1_RIGHT, MapCell.TANK_2_LVL_4_STATE_2_RIGHT};
			default:
				return new MapCell[]{MapCell.TANK_2_LVL_1_STATE_1_RIGHT, MapCell.TANK_2_LVL_1_STATE_2_RIGHT};
		}
	}
	public static MapCell[] player2DownState(int level){
		switch(level){
			case 2:
				return new MapCell[]{MapCell.TANK_2_LVL_2_STATE_1_DOWN, MapCell.TANK_2_LVL_2_STATE_2_DOWN};
			case 3:
				return new MapCell[]{MapCell.TANK_2_LVL_3_STATE_1_DOWN, MapCell.TANK_2_LVL_3_STATE_2_DOWN};
			case 4:
				return new MapCell[]{MapCell.TANK_2_LVL_4_STATE_1_DOWN, MapCell.TANK_2_LVL_4_STATE_2_DOWN};
			default:
				return new MapCell[]{MapCell.TANK_2_LVL_1_STATE_1_DOWN, MapCell.TANK_2_LVL_1_STATE_2_DOWN};
		}
	}
	public static MapCell[] player2LeftState(int level){
		switch(level){
			case 2:
				return new MapCell[]{MapCell.TANK_2_LVL_2_STATE_1_LEFT, MapCell.TANK_2_LVL_2_STATE_2_LEFT};
			case 3:
				return new MapCell[]{MapCell.TANK_2_LVL_3_STATE_1_LEFT, MapCell.TANK_2_LVL_3_STATE_2_LEFT};
			case 4:
				return new MapCell[]{MapCell.TANK_2_LVL_4_STATE_1_LEFT, MapCell.TANK_2_LVL_4_STATE_2_LEFT};
			default:
				return new MapCell[]{MapCell.TANK_2_LVL_1_STATE_1_LEFT, MapCell.TANK_2_LVL_1_STATE_2_LEFT};
		}
	}
}
