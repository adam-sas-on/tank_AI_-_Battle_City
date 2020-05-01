package com.company;

import com.company.view.GameView;
import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;


public class Main extends Application {
	Game game;

	@Override
	public void start(Stage primaryStage) throws Exception{
		GameView view = new GameView();

		game = new Game(view);

		Scene scene = game.start();

		primaryStage.setScene(scene);
		scene.addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyPressed);
		scene.addEventFilter(KeyEvent.KEY_RELEASED, this::onKeyReleased);
		//scene.addEventHandler();

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent windowEvent) {
				game.stop();
				Platform.exit();
				System.exit(0);
			}
		});

		primaryStage.setTitle("Battle City - java");
		primaryStage.show();
	}


	public static void main(String[] args) {
		launch(args);
	}

	private void onKeyPressed(KeyEvent event){
		game.driveTank(event);
		event.consume();
	}

	private void onKeyReleased(KeyEvent event){
		game.stopTanks(event);
	}
}
