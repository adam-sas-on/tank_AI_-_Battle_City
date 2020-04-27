package com.company;

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
	private static Queue<KeyCode> eventCodes;
	private static int eventsSize;
	private final int msInterval = 20;
	private static boolean pause;

	private static PlayerAITank player1;
	private static PlayerAITank player2;

	public Game(GameView view){
		Game.view = view;
		runGame = Executors.newSingleThreadScheduledExecutor();

		int tankCellSize = MapCell.TANK_1_LVL_1_STATE_1_UP.getSize();

		eventCodes = new LinkedList<>();
		eventsSize = 0;
		pause = false;

		player1 = new PlayerAITank(20, tankCellSize);
		player1.setPosOnPlayer1(tankCellSize);
		setPlayerIcons();
		player2 = new PlayerAITank(20, tankCellSize);
		player2.setPosOnPlayer2(tankCellSize);

		view.loadMapSetPlayers("map_1.txt", player1, player2);
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
	}

	public Scene start(){
		Scene scene = view.drawStart();
		view.drawMap(player1);

		runGame.scheduleAtFixedRate(Game::run, 0, msInterval, TimeUnit.MILLISECONDS);

		return scene;
	}

	public void driveTank(KeyEvent keyEvent){
		if(eventsSize < 5) {
			eventCodes.add(keyEvent.getCode());
			eventsSize++;
		}
	}

	public static void run(){
		if(!eventCodes.isEmpty() ) {
			KeyCode eventCode = eventCodes.poll();
			player1.move(eventCode, view);

			switch(eventCode){
				case N:// shot;
					System.out.println("N");
					break;
				case C:
					pause = !pause;
					break;
			}

			eventsSize--;
			view.drawMap(player1);
		}

		if(pause)
			return;

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
