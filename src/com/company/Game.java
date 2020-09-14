package com.company;

import com.company.movement.BattleRandom;
import com.company.movement.LearningAIClass;
import com.company.movement.TankAI;
import com.company.model.PlayerAITank;
import com.company.view.GameView;
import com.company.dao.MapLoader;
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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Game {
	private GameView view;
	private final ScheduledExecutorService runGame;
	private ScheduledFuture<?> runSchedule;
	private final int msInterval = 20;
	private GameDynamics dynamics;
	private final Timeline timeline;
	private boolean pause, mapFinished;
	private boolean aiNotUpdated, trainingAI, aiWaiting, quietTraining;

	private MapLoader mapLoader;
	private List<String> maps;
	private int mapNumber;
	BattleRandom rand;

	private SpriteEventController player1driver, player2driver;
	private PlayerAITank player1;
	private PlayerAITank player2;
	private TankAI allyAI1;
	private TankAI allyAI2;
	private LearningAIClass machineLearning;

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
		pause = mapFinished = true;
		aiNotUpdated = true;
		trainingAI = quietTraining = false;

		setControllers(cellPrecisionUnitSize);

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
		this.view.drawMap(dynamics);

		// Cannot assign a value to final variable 'timeline'
		timeline = new Timeline(
				new KeyFrame(Duration.ZERO, new EventHandler<>(){
					@Override
					public void handle(ActionEvent actionEvent){
						view.refresh(dynamics);
					}
				}),
				new KeyFrame(Duration.millis(20))
		);
	}

	private void setControllers(int cellPrecisionUnitSize){
		player1driver = new SpriteEventController(KeyCode.UP, KeyCode.RIGHT, KeyCode.DOWN, KeyCode.LEFT,
				KeyCode.N, KeyCode.COMMA, KeyCode.M, KeyCode.PERIOD);

		player2driver = new SpriteEventController(KeyCode.W, KeyCode.D, KeyCode.S, KeyCode.A,
				KeyCode.T, KeyCode.G, KeyCode.R, KeyCode.F);

		machineLearning = new LearningAIClass(rand, cellPrecisionUnitSize);
		boolean success = machineLearning.readFile();
		if(!success)
			machineLearning.setDefaultLearningPopulation();

		allyAI1 = new TankAI(rand, 2, cellPrecisionUnitSize);
		allyAI2 = machineLearning.getCurrentProcessed();
		allyAI1.resetByOtherNN(allyAI2);

		player1driver.setAI(allyAI1);
		player2driver.setAI(allyAI2);
	}

	private void setPlayerIcons(){
		player1.setIcons();

		// - - - player 2nd;
		player2.setIcons();
	}

	private void upDateAI(){
		boolean aiUsed = player1driver.isDriveByAI() || player2driver.isDriveByAI();
		if(aiNotUpdated && aiUsed){
			aiNotUpdated = false;
			player1.updateActionPoints();
			player2.updateActionPoints();

			machineLearning.updateAI();
			machineLearning.weightedSelection();
		}
	}

	private void startStopTrainingAI(MouseEvent mouseEvent){
		if(!player1driver.isDriveByAI() && !player2driver.isDriveByAI() )
			return;

		trainingAI = !trainingAI;
		view.startStopTrainingAI();

		if(!trainingAI) {
			pause = true;
			view.pauseDrawing();
			dynamics.resetTheGame();
		}
	}
	private void animateAI(MouseEvent mouseEvent){
		view.startStopAIAnimation();
		quietTraining = !quietTraining;

		if(runSchedule != null){
			runSchedule.cancel(false);// true/false - may interrupt if running;
		}

		if(quietTraining){
			timeline.stop();

			runSchedule = runGame.scheduleWithFixedDelay(this::runTrainingAI, 0, msInterval/2, TimeUnit.MILLISECONDS);
		} else {
			timeline.play();

			runSchedule = runGame.scheduleAtFixedRate(this::run, 0, msInterval, TimeUnit.MILLISECONDS);
		}

	}


	private void switchPlayers1AI(MouseEvent mouseEvent){
		player1driver.switchPlayerAI();
		view.switchAI(true);
	}
	private void switchPlayers2AI(MouseEvent mouseEvent){
		player2driver.switchPlayerAI();
		view.switchAI(false);
	}

	private void switchPlayingFor1st(MouseEvent mouseEvent){
		dynamics.setUnsetPlaying1stPlayer();
		view.switchPlayers1stPlaying();
	}
	private void switchPlayingFor2nd(MouseEvent mouseEvent){
		dynamics.setUnsetPlaying2ndPlayer();
		view.switchPlayers2ndPlaying();
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

	private void loadMap(int mapIndex){
		mapNumber = mapIndex;
		dynamics.loadMap(maps.get(mapNumber), mapLoader, view);
		pause = false;
		view.blockMenuForPlaying();
		view.keepDrawing();
		aiNotUpdated = true;
	}

	private void loadMapFromList(MouseEvent mouseEvent){
		String map = view.getSelectedMap();
		int mapIndex = maps.indexOf(map);
		if(mapIndex < 0)
			System.out.println("Can not get map called  " + map);
		else {
			loadMap(mapIndex);
		}
	}

	public void start(){
		view.getStartPauseButton().addEventHandler(MouseEvent.MOUSE_CLICKED, this::startPauseGameByMouse);

		view.getTrainingAIButton().addEventHandler(MouseEvent.MOUSE_CLICKED, this::startStopTrainingAI);
		view.getAnimatingAI().addEventHandler(MouseEvent.MOUSE_CLICKED, this::animateAI);

		view.getPlayersAI_switch(true).addEventHandler(MouseEvent.MOUSE_CLICKED, this::switchPlayers1AI);
		view.getPlayersAI_switch(false).addEventHandler(MouseEvent.MOUSE_CLICKED, this::switchPlayers2AI);

		view.get1stPlayerStopButton().addEventHandler(MouseEvent.MOUSE_CLICKED, this::switchPlayingFor1st);
		view.get2ndPlayerStopButton().addEventHandler(MouseEvent.MOUSE_CLICKED, this::switchPlayingFor2nd);


		view.getLoadingMapButton().addEventHandler(MouseEvent.MOUSE_CLICKED, this::loadMapFromList);

		view.getResetButton().addEventHandler(MouseEvent.MOUSE_CLICKED, this::resetGame);

		timeline.setCycleCount(Timeline.INDEFINITE);

		runSchedule = runGame.scheduleAtFixedRate(this::run, 0, msInterval, TimeUnit.MILLISECONDS);// this::run -> new Runnable(){}
		//runGame.scheduleAtFixedRate(this::run, 0, msInterval, TimeUnit.MILLISECONDS);// this::run -> new Runnable(){}
		timeline.play();
	}

	public void listen(KeyEvent keyEvent){
		KeyCode keyCode = keyEvent.getCode();
		if(keyCode == KeyCode.C){
			startPauseGame();
			if(mapFinished && !trainingAI){
				loadMapFromList(null);
				mapFinished = false;
				aiNotUpdated = true;
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
		else if(trainingAI){
			runTrainingAI();
			return;
		}

		keepRunning = dynamics.nextStep();
		mapFinished = dynamics.isMapFinished();
		if(!keepRunning){
			pause = true;
			view.typeText("Game Over!");// runLater
			view.pauseDrawing();
			mapNumber++;
			if(mapNumber == maps.size() )
				mapNumber = 0;
			view.selectNextMap();
			view.getResetButton().setDisable(false);

			upDateAI();
		} else if(mapFinished){
			pause = true;
			view.typeText("Map finished");
			mapNumber++;
			if(mapNumber == maps.size() )
				mapNumber = 0;
			view.unblockMenuForPlaying();
			//dynamics.loadMap(maps.get(mapNumber), mapLoader, view);
			view.selectNextMap();

			upDateAI();
		}
	}

	public void runTrainingAI(){
		/*if(pause)
			return;*/

		if(aiWaiting){
			aiWaiting = view.keepCountingForAI(dynamics);
			if(!aiWaiting){
				dynamics.resetTheGame();
				if(!mapFinished)
					dynamics.resetTheGame();
				loadMap(mapNumber);
			}
			return;
		}

		boolean keepRunning = dynamics.nextStep();
		mapFinished = dynamics.isMapFinished();
		if(!keepRunning || mapFinished){
			aiWaiting = true;
			view.startCountingForAI();
			upDateAI();
		}
	}

	public void stop(){
		timeline.stop();

		runGame.shutdown();
		try {
			runGame.awaitTermination(1, TimeUnit.SECONDS);
		} catch(InterruptedException ignore){}
		runGame.shutdownNow();

		if( machineLearning.wasMLUpdated() )
			machineLearning.writeFile();
	}

	private void resetGame(MouseEvent mouseEvent){
		trainingAI = false;
		dynamics.resetTheGame();
		pause = true;
		view.pauseDrawing();
		mapFinished = false;
		mapNumber = 1;
		// reload map;
		view.unblockMenuForPlaying();
	}
}
