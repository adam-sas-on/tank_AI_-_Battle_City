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
	private final int cellPrecisionUnitSize = 10000;
	private static GameDynamics dynamics;
	private static boolean pause;

	private static MapLoader mapLoader;
	private List<String> maps;

	private static SpriteEventController player1driver, player2driver;
	private static PlayerAITank player1;
	private static PlayerAITank player2;

	public Game(GameView view){
		Game.view = view;
		mapLoader = MapLoader.getInstance();
		maps = new ArrayList<>();
		mapLoader.getFileList(maps);

		runGame = Executors.newSingleThreadScheduledExecutor();
		dynamics = new GameDynamics(mapLoader.getMaxCols(), mapLoader.getMaxRows(), cellPrecisionUnitSize);
		pause = false;


		setControllers();

		player1 = new PlayerAITank(player1driver, 20, cellPrecisionUnitSize);
		player1.setDefaultPlayerPosition();

		player2 = new PlayerAITank(player2driver, 20, cellPrecisionUnitSize);
		player2.setDefaultPlayerPosition();
		setPlayerIcons();
		dynamics.setFirstPlayer(player1);
		dynamics.setSecondPlayer(player2);

		dynamics.loadMap(maps.get(6), mapLoader, view);

		//view.loadMapSetPlayers("map_2.txt", player1, player2);
		//view.addCell(player1.getCell());
		//view.addCell(player2.getCell());
	}

	private void setControllers(){
		player1driver = new SpriteEventController(KeyCode.UP, KeyCode.RIGHT, KeyCode.DOWN, KeyCode.LEFT,
				KeyCode.N, KeyCode.COMMA, KeyCode.M, KeyCode.PERIOD);

		player2driver = new SpriteEventController(KeyCode.W, KeyCode.D, KeyCode.S, KeyCode.A,
				KeyCode.T, KeyCode.G, KeyCode.R, KeyCode.F);
	}

	private void setPlayerIcons(){
		player1.setIcons();

		// - - - player 2nd;
		player2.setIcons();
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
		} else if(pause)
			return;

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

		dynamics.nextStep();

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
