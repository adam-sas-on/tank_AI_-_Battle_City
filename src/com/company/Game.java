package com.company;

import com.company.model.PlayerAITank;
import com.company.view.GameView;
import com.company.view.MapLoader;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
	private boolean pause;

	private MapLoader mapLoader;
	private List<String> maps;

	private SpriteEventController player1driver, player2driver;
	private PlayerAITank player1;
	private PlayerAITank player2;

	public Game(GameView view){
		this.view = view;
		mapLoader = MapLoader.getInstance();
		maps = new ArrayList<>();
		mapLoader.getFileList(maps);

		runGame = Executors.newSingleThreadScheduledExecutor();
		dynamics = new GameDynamics(mapLoader.getMaxCols(), mapLoader.getMaxRows(), view);
		pause = false;

		setControllers();

		int cellPrecisionUnitSize = this.view.getDefaultCellSize();
		player1 = new PlayerAITank(player1driver, 20, cellPrecisionUnitSize);
		player1.setDefaultPlayerPosition();

		player2 = new PlayerAITank(player2driver, 20, cellPrecisionUnitSize);
		player2.setDefaultPlayerPosition();
		setPlayerIcons();
		dynamics.setFirstPlayer(player1);
		dynamics.setSecondPlayer(player2);

		dynamics.loadMap(maps.get(14), mapLoader, view);

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

	public void start(){
		timeline.setCycleCount(Timeline.INDEFINITE);

		runGame.scheduleAtFixedRate(this::run, 0, msInterval, TimeUnit.MILLISECONDS);
		timeline.play();
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

	public void run(){
		//boolean watch = false;

		if(pause)
			return;

		dynamics.nextStep();

	}

	public void stop(){
		timeline.stop();
		runGame.shutdown();
		try {
			runGame.awaitTermination(1, TimeUnit.SECONDS);
		} catch(InterruptedException ignore){}
		runGame.shutdownNow();
	}
}
