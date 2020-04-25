package com.company;

import com.company.view.GameView;
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


	public Game(GameView view){
		Game.view = view;
		runGame = Executors.newSingleThreadScheduledExecutor();


		eventCodes = new LinkedList<>();
		eventsSize = 0;
		pause = false;
	}

	public Scene start(){
		Scene scene = view.drawStart();
		view.drawMap(null);

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

			switch(eventCode){
				case N:// shot;
					System.out.println("N");
					break;
				case C:
					pause = !pause;
					break;
			}

			eventsSize--;
			view.drawMap(null);
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
