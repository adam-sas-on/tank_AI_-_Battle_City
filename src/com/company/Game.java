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

	private static PlayerAITank player1;
	private static PlayerAITank player2;

	public Game(GameView view){
		Game.view = view;
		runGame = Executors.newSingleThreadScheduledExecutor();

		eventCodes1 = new LinkedList<>();
		eventCodes2 = new LinkedList<>();
		eventsSize1 = eventsSize2 = 0;
		pause = false;

		player1 = new PlayerAITank(20, tankCellSize);
		player1.setPosOnPlayer1(tankCellSize);

		player2 = new PlayerAITank(20, tankCellSize);
		player2.setPosOnPlayer2(tankCellSize);
		setPlayerIcons();

		view.loadMapSetPlayers("map_2.txt", player1, player2);
		view.addCell(player1.getCell());
		view.addCell(player2.getCell());

		damages = DamageClass.getInstance();
	}

	private void setPlayerIcons(){
		MapCell[] cells = new MapCell[]{MapCell.TANK_1_LVL_1_STATE_1_UP, MapCell.TANK_1_LVL_1_STATE_2_UP};
		player1.addIcons(KeyCode.UP, cells);

		cells = new MapCell[]{MapCell.TANK_1_LVL_1_STATE_1_RIGHT, MapCell.TANK_1_LVL_1_STATE_2_RIGHT};
		player1.addIcons(KeyCode.RIGHT, cells);

		cells = new MapCell[]{MapCell.TANK_1_LVL_1_STATE_1_DOWN, MapCell.TANK_1_LVL_1_STATE_2_DOWN};
		player1.addIcons(KeyCode.DOWN, cells);

		cells = new MapCell[]{MapCell.TANK_1_LVL_1_STATE_1_LEFT, MapCell.TANK_1_LVL_1_STATE_2_LEFT};
		player1.addIcons(KeyCode.LEFT, cells);

		// - - - player 2nd;
		cells = new MapCell[]{MapCell.TANK_2_LVL_1_STATE_1_UP, MapCell.TANK_2_LVL_1_STATE_2_UP};
		player2.addIcons(KeyCode.UP, cells);

		cells = new MapCell[]{MapCell.TANK_2_LVL_1_STATE_1_RIGHT, MapCell.TANK_2_LVL_1_STATE_2_RIGHT};
		player2.addIcons(KeyCode.RIGHT, cells);

		cells = new MapCell[]{MapCell.TANK_2_LVL_1_STATE_1_DOWN, MapCell.TANK_2_LVL_1_STATE_2_DOWN};
		player2.addIcons(KeyCode.DOWN, cells);

		cells = new MapCell[]{MapCell.TANK_2_LVL_1_STATE_1_LEFT, MapCell.TANK_2_LVL_1_STATE_2_LEFT};
		player2.addIcons(KeyCode.LEFT, cells);
	}

	public Scene start(){
		Scene scene = view.drawStart();
		view.drawMap();

		runGame.scheduleAtFixedRate(Game::run, 0, msInterval, TimeUnit.MILLISECONDS);

		return scene;
	}

	public void driveTank(KeyEvent keyEvent){
		if(eventsSize1 >= 5 && eventsSize2 >= 5)
			return;

		KeyCode keyCode = keyEvent.getCode();
		switch(keyCode){
			case UP:
			case RIGHT:
			case DOWN:
			case LEFT:
			case N:
			case COMMA:
			case C:
				if(eventsSize1 < 5)
					eventCodes1.add(keyCode);
				break;
		}

		if(eventsSize2 < 5){
			switch(keyCode){
				case W:
					eventCodes2.add(KeyCode.UP);
					break;
				case A:
					eventCodes2.add(KeyCode.LEFT);
					break;
				case S:
					eventCodes2.add(KeyCode.DOWN);
					break;
				case D:
					eventCodes2.add(KeyCode.RIGHT);
					break;
				case Q:
				case R:
					eventCodes2.add(keyCode);
					break;
			}
		}
	}

	public static void run(){
		boolean watch = false;
		Bullet bullet;
		KeyCode eventCode;
		if(!eventCodes1.isEmpty() ){
			eventCode = eventCodes1.poll();
			eventsSize1--;

			switch(eventCode){
				case UP:
				case RIGHT:
				case DOWN:
				case LEFT:
					player1.move(eventCode, view);
					break;
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
			//view.drawMap();
		}

		if(!eventCodes2.isEmpty() ){
			eventCode = eventCodes2.poll();
			eventsSize2--;

			switch(eventCode){
				case UP:
				case RIGHT:
				case DOWN:
				case LEFT:
					player2.move(eventCode, view);
					break;
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
		}

		if(pause)
			return;
		if(watch)
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
