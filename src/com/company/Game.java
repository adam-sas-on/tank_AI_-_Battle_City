package com.company;

import com.company.model.Bullet;
import com.company.model.DamageClass;
import com.company.model.PlayerAITank;
import com.company.view.GameView;
import com.company.view.MapCell;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Game {
	private static GameView view;
	private final ScheduledExecutorService runGame;
	private static Queue<KeyCode> eventCodes1, eventCodes2;
	private static int eventsSize1, eventsSize2;
	private static final int msInterval = 20;
	private static final int tankCellSize = MapCell.TANK_1_LVL_1_STATE_1_UP.getSize();
	private static boolean pause;
	private static DamageClass damages;

	private static SpriteEventController player1driver, player2driver;
	private static PlayerAITank player1;
	private static PlayerAITank player2;

	public Game(GameView view){
		Game.view = view;
		runGame = Executors.newSingleThreadScheduledExecutor();

		eventCodes1 = new LinkedList<>();
		eventCodes2 = new LinkedList<>();
		eventsSize1 = eventsSize2 = 0;
		pause = false;

		setControllers();

		player1 = new PlayerAITank(20, tankCellSize, player1driver);
		player1.setPosOnPlayer1();

		player2 = new PlayerAITank(20, tankCellSize, player2driver);
		player2.setPosOnPlayer2();
		setPlayerIcons();

		view.loadMapSetPlayers("map_2.txt", player1, player2);
		view.addCell(player1.getCell());
		view.addCell(player2.getCell());

		damages = DamageClass.getInstance();
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
		view.drawMap();

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
		Bullet bullet;
		KeyCode eventCode;
		/*if(!eventCodes1.isEmpty() ){
			eventCode = eventCodes1.poll();
			eventsSize1--;

			switch(eventCode){
				case N:// shot;
					bullet = player1.fireBullet(msInterval, tankCellSize, damages);
					view.addBullet(bullet);
					break;
				case COMMA:// weak shot;
					bullet = player1.fireBullet(msInterval, tankCellSize, damages);
					bullet.makeWeak();
					view.addBullet(bullet);
					break;
				case C:
					pause = !pause;
					break;
			}

			watch = true;
			/ /view.drawMap();
		}* /

		/ *if(!eventCodes2.isEmpty() ){
			eventCode = eventCodes2.poll();
			eventsSize2--;

			switch(eventCode){
				case R:// shot;
					bullet = player2.fireBullet(msInterval, tankCellSize, damages);
					view.addBullet(bullet);
					break;
				case Q:// weak shot;
					bullet = player2.fireBullet(msInterval, tankCellSize, damages);
					bullet.makeWeak();
					view.addBullet(bullet);
					break;
			}
			watch = true;
		}*/

		if(pause)
			return;

		player1.move(view);
		bullet = player1.fireBullet(tankCellSize, damages);
		if(bullet != null)
			view.addBullet(bullet);

		player2.move(view);
		bullet = player2.fireBullet(tankCellSize, damages);
		if(bullet != null)
			view.addBullet(bullet);
		//if(watch)
			view.drawMap();
		// run sprites and AI;
	}

	public void stop(){
		runGame.shutdown();
		try {
			runGame.awaitTermination(1, TimeUnit.SECONDS);
		} catch(InterruptedException ignore){}
		runGame.shutdownNow();
	}
}
