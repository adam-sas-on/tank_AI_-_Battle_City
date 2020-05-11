package com.company;

import com.company.model.PlayerAITank;
import com.company.view.GameView;
import com.company.view.MapCell;
import com.company.view.MapLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Game {
	private static GameView view;
	private final ScheduledExecutorService runGame;
	private static final int msInterval = 20;
	private static GameDynamics dynamics;
	private static boolean pause;

	private static MapLoader mapLoader;
	private List<String> maps;

	private static SpriteEventController player1driver, player2driver;
	private static PlayerAITank player1;
	private static PlayerAITank player2;

	public Game(GameView view){
		Game.view = view;
		runGame = Executors.newSingleThreadScheduledExecutor();
		dynamics = new GameDynamics(26, 26);
		pause = false;

		mapLoader = MapLoader.getInstance();
		maps = new ArrayList<>();
		mapLoader.getFileList(maps);

		setControllers();

		player1 = new PlayerAITank(20, player1driver);
		player1.setPosOnPlayer1();

		player2 = new PlayerAITank(20, player2driver);
		player2.setPosOnPlayer2();
		setPlayerIcons();
		dynamics.setFirstPlayer(player1);
		dynamics.setSecondPlayer(player2);

		dynamics.loadMap(maps.get(2), mapLoader);

		view.loadMapSetPlayers("map_2.txt", player1, player2);
		view.addCell(player1.getCell());
		view.addCell(player2.getCell());
	}

	private void setControllers(){
		player1driver = new SpriteEventController(KeyCode.UP, KeyCode.RIGHT, KeyCode.DOWN, KeyCode.LEFT,
				KeyCode.N, KeyCode.COMMA, KeyCode.M, KeyCode.PERIOD);

		player2driver = new SpriteEventController(KeyCode.W, KeyCode.D, KeyCode.S, KeyCode.A,
				KeyCode.T, KeyCode.G, KeyCode.R, KeyCode.F);
	}

	private void setPlayerIcons(){
		MapCell[] cells = new MapCell[]{MapCell.TANK_1_LVL_1_STATE_1_UP, MapCell.TANK_1_LVL_1_STATE_2_UP};
		int directionAngle = player1driver.moveKeyValue(KeyCode.UP);
		player1.addIcons(directionAngle, cells);

		cells = new MapCell[]{MapCell.TANK_1_LVL_1_STATE_1_RIGHT, MapCell.TANK_1_LVL_1_STATE_2_RIGHT};
		directionAngle = player1driver.moveKeyValue(KeyCode.RIGHT);
		player1.addIcons(directionAngle, cells);

		cells = new MapCell[]{MapCell.TANK_1_LVL_1_STATE_1_DOWN, MapCell.TANK_1_LVL_1_STATE_2_DOWN};
		directionAngle = player1driver.moveKeyValue(KeyCode.DOWN);
		player1.addIcons(directionAngle, cells);

		cells = new MapCell[]{MapCell.TANK_1_LVL_1_STATE_1_LEFT, MapCell.TANK_1_LVL_1_STATE_2_LEFT};
		directionAngle = player1driver.moveKeyValue(KeyCode.LEFT);
		player1.addIcons(directionAngle, cells);

		// - - - player 2nd;
		cells = new MapCell[]{MapCell.TANK_2_LVL_1_STATE_1_UP, MapCell.TANK_2_LVL_1_STATE_2_UP};
		directionAngle = player2driver.moveKeyValue(KeyCode.W);
		player2.addIcons(directionAngle, cells);

		cells = new MapCell[]{MapCell.TANK_2_LVL_1_STATE_1_RIGHT, MapCell.TANK_2_LVL_1_STATE_2_RIGHT};
		directionAngle = player2driver.moveKeyValue(KeyCode.D);
		player2.addIcons(directionAngle, cells);

		cells = new MapCell[]{MapCell.TANK_2_LVL_1_STATE_1_DOWN, MapCell.TANK_2_LVL_1_STATE_2_DOWN};
		directionAngle = player2driver.moveKeyValue(KeyCode.S);
		player2.addIcons(directionAngle, cells);

		cells = new MapCell[]{MapCell.TANK_2_LVL_1_STATE_1_LEFT, MapCell.TANK_2_LVL_1_STATE_2_LEFT};
		directionAngle = player2driver.moveKeyValue(KeyCode.A);
		player2.addIcons(directionAngle, cells);
	}

	public Scene start(){
		Scene scene = view.drawStart();
		//view.drawMap();

		runGame.scheduleAtFixedRate(Game::run, 0, msInterval, TimeUnit.MILLISECONDS);

		return scene;
	}

	public void listen(KeyEvent keyEvent){
		KeyCode keyCode = keyEvent.getCode();
		if(keyCode == KeyCode.C){
			pause = !pause;
			return;
		}

		player1driver.setEvent(keyCode);
		player2driver.setEvent(keyCode);
	}

	public void stopTanks(KeyEvent keyEvent){
		KeyCode keyCode = keyEvent.getCode();
		player1driver.stopEvent(keyCode);
		player2driver.stopEvent(keyCode);
	}

	public static void run(){
		boolean watch = false;

		if(pause)
			return;

		dynamics.nextStep(view);

		//if(watch)
			view.drawMap(dynamics);
	}

	public void stop(){
		runGame.shutdown();
		try {
			runGame.awaitTermination(1, TimeUnit.SECONDS);
		} catch(InterruptedException ignore){}
		runGame.shutdownNow();
	}
}
