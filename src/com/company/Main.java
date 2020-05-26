package com.company;

import com.company.view.GameView;
import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

		Scene scene = view.drawStart();

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

		handleResize(primaryStage, view);

		primaryStage.setTitle("Battle City - java");
		primaryStage.show();
		game.start();
	}


	public static void main(String[] args) {
		launch(args);
	}

	private void onKeyPressed(KeyEvent event){
		game.listen(event);
		event.consume();
	}

	private void onKeyReleased(KeyEvent event){
		game.stopTanks(event);
	}

	private void handleResize(Stage stage, GameView view){
		stage.widthProperty().addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1){
				view.modifyCellSize((int)stage.getWidth(), (int)stage.getHeight());
			}
		});

		stage.heightProperty().addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1){
				view.modifyCellSize((int)stage.getWidth(), (int)stage.getHeight());
			}
		});
	}

}
