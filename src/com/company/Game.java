package com.company;

import com.company.logic.BattleRandom;
import com.company.model.PlayerAITank;
import com.company.view.GameView;
import com.company.view.MapLoader;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Game {
	private GameView view;
	private final ScheduledExecutorService runGame;
	private final int msInterval = 20;
	private GameDynamics dynamics;
	private final Timeline timeline;
	private boolean pause, mapFinished;

	private MapLoader mapLoader;
	private List<String> maps;
	private int mapNumber;
	BattleRandom rand;

	private SpriteEventController player1driver, player2driver;
	private PlayerAITank player1;
	private PlayerAITank player2;

	public Game(GameView view){
		this.view = view;
		this.view.setFramesPerSeconds(msInterval);
		int cellPrecisionUnitSize = this.view.getDefaultCellSize();

		rand = new BattleRandom(cellPrecisionUnitSize);
		mapLoader = MapLoader.getInstance();
		mapLoader.setRandom(rand);
		maps = new ArrayList<>();
		mapLoader.getFileList(maps);
		this.view.addMaps(maps);

		runGame = Executors.newSingleThreadScheduledExecutor();
		dynamics = new GameDynamics(mapLoader, view, rand);
		pause = mapFinished = false;

		setControllers();

		player1 = new PlayerAITank(player1driver, this.view);
		player1.setDefaultPlayerPosition();

		player2 = new PlayerAITank(player2driver, this.view);
		player2.setDefaultPlayerPosition();
		setPlayerIcons();
		dynamics.setFirstPlayer(player1);
		dynamics.setSecondPlayer(player2);

		mapNumber = 1;
		dynamics.loadMap(maps.get(mapNumber), mapLoader, view);
		this.view.selectMap(maps.get(mapNumber));

		// Cannot assign a value to final variable 'timeline'
		timeline = new Timeline(
				new KeyFrame(Duration.ZERO, new EventHandler<>(){
					@Override
					public void handle(ActionEvent actionEvent){
						view.drawMap(dynamics);
					}
				}),
				new KeyFrame(Duration.millis(20))
		);
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

	private void startPauseGame(){
		pause = !pause;

		if(pause)
			view.pauseDrawing();
		else
			view.keepDrawing();
	}
	private void startPauseGameByMouse(MouseEvent mouseEvent){
		startPauseGame();
	}

	private void loadMapFromList(MouseEvent mouseEvent){
		String map = view.getSelectedMap();
		int mapIndex = maps.indexOf(map);
		if(mapIndex < 0)
			System.out.println("Can not get map called  " + map);
		else {
			mapNumber = mapIndex;
			dynamics.loadMap(maps.get(mapNumber), mapLoader, view);
			pause = false;
			view.getLoadingMapButton().setDisable(true);
			view.keepDrawing();
		}
	}

	public void start(){
		view.getStartPauseButton().addEventHandler(MouseEvent.MOUSE_CLICKED, this::startPauseGameByMouse);

		view.getLoadingMapButton().addEventHandler(MouseEvent.MOUSE_CLICKED, this::loadMapFromList);

		view.getResetButton().addEventHandler(MouseEvent.MOUSE_CLICKED, this::resetGame);

		timeline.setCycleCount(Timeline.INDEFINITE);

		runGame.scheduleAtFixedRate(this::run, 0, msInterval, TimeUnit.MILLISECONDS);
		timeline.play();
	}

	public void listen(KeyEvent keyEvent){
		KeyCode keyCode = keyEvent.getCode();
		if(keyCode == KeyCode.C){
			startPauseGame();
			if(mapFinished){
				loadMapFromList(null);
				mapFinished = false;
			}

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

	public void run(){
		boolean keepRunning;

		if(pause)
			return;

		keepRunning = dynamics.nextStep();
		mapFinished = dynamics.isMapFinished();
		if(!keepRunning){
			pause = true;
			view.typeText("Game Over!");
			//view.pauseDrawing();
			mapNumber++;
			if(mapNumber == maps.size() )
				mapNumber = 0;
			view.selectNextMap();
		} else if(mapFinished){
			pause = true;
			view.typeText("Map finished");
			mapNumber++;
			if(mapNumber == maps.size() )
				mapNumber = 0;
			view.getLoadingMapButton().setDisable(false);
			//dynamics.loadMap(maps.get(mapNumber), mapLoader, view);
			view.selectNextMap();
		}
	}

	public void stop(){
		timeline.stop();
		runGame.shutdown();
		try {
			runGame.awaitTermination(1, TimeUnit.SECONDS);
		} catch(InterruptedException ignore){}
		runGame.shutdownNow();
	}

	private void resetGame(MouseEvent mouseEvent){
		dynamics.resetTheGame();
		pause = false;
	}
}
