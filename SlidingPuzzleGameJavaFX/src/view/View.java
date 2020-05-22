package view;

import controller.Controller;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.File;
import java.util.Timer;

// TODO: Tests, Exceptions

public class View extends Application {

    private Controller ctrl = new Controller();
    private Scene gameScene, endScene, menuScene;
    private Stage window;
    private Integer maxSeconds = 180;
    private Integer seconds = maxSeconds;
    private Label timeLabel = new Label();
    private GridPane grid;



    public void menu(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        window = primaryStage;
        window.setTitle("Sliding Puzzle Game Experiment");

        //   start menu stuff
        initStartMenu();

        //   end game stuff
        initEndGame();

        window.setScene(menuScene);
        window.show();

    }

    private void initEndGame(){
        VBox endMenu = new VBox();
        Label endLabel = new Label("END of th GAME (it was inevitable)");
        Button restart = new Button("im addicted");
        restart.setOnAction(e -> {
            ctrl.shufflePieces();
            seconds = maxSeconds;
            window.setScene(gameScene);
        });
        Button commitDie = new Button("unstart the game");

        commitDie.setOnAction(e -> window.close());
        endMenu.getChildren().addAll(endLabel, restart, commitDie);
        endMenu.setAlignment(Pos.CENTER);
        endMenu.setSpacing(30);
        endScene = new Scene(endMenu, 1080, 720);
    }

    private void initStartMenu(){

        VBox startMenu = new VBox();
        Label label = new Label("Press Any Button to Start");
        label.setFont(new Font("Calibri", 30));

        TextField nrPiese = new TextField();
        nrPiese.setMaxWidth(150);


        Button startButton = new Button("Any");
        startButton.setMinWidth(150);
        startButton.setMinHeight(75);
        startButton.setOnAction(e -> {
            grid = pickImageAndSize(Integer.parseInt(nrPiese.getText().toString()));
            initGameScene(grid);
            window.setScene(gameScene);
            initTimer();
        });
        startMenu.getChildren().addAll(label, nrPiese, startButton);
        startMenu.setAlignment(Pos.CENTER);
        startMenu.setSpacing(30);

        menuScene = new Scene(startMenu, 1080, 720);
    }

    private void initGameScene(GridPane grid){
        BorderPane layout = new BorderPane();

        HBox hbox1 = new HBox(20);
        hbox1.setPadding(new Insets(10, 10, 10, 10));
        hbox1.getChildren().addAll(timeLabel);
        hbox1.setAlignment(Pos.CENTER);

        layout.setTop(hbox1);
        layout.setCenter(grid);

        gameScene = new Scene(layout, 1080, 720);
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            //inverted so the movement appears to be of the pics next to the empty one
            if(ctrl.solved()){
                window.setScene(endScene);
            }
            if(key.getCode() == KeyCode.RIGHT){
                ctrl.moveEmptyPiece(new Pair<>(0, -1));
            }
            if(key.getCode() == KeyCode.LEFT){
                ctrl.moveEmptyPiece(new Pair<>(0, 1));
            }
            if(key.getCode() == KeyCode.UP){
                ctrl.moveEmptyPiece(new Pair<>(1, 0));
            }
            if(key.getCode() == KeyCode.DOWN){
                ctrl.moveEmptyPiece(new Pair<>(-1, 0));
            }

        });
    }

    private void initTimer(){
        Timeline time = new Timeline();
        KeyFrame frame = new KeyFrame(
                Duration.seconds(1),
                e -> {
                    seconds--;
                    Integer minutes = seconds/60;
                    Integer leftSeconds = seconds%60;
                    timeLabel.setText("Time : " + minutes.toString() + ":" + leftSeconds.toString());
                    if(seconds <= 0){
                        window.setScene(endScene);
                    }
                }
        );
        time.setCycleCount(Timeline.INDEFINITE);
        time.getKeyFrames().add(frame);
        if(time != null){
            time.stop();
        }
        time.play();
    }

    private GridPane pickImageAndSize(int n){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("F:\\Java\\Java facultate\\SlidingPuzzleGameJavaFX\\resources"));
        File file = fileChooser.showOpenDialog(window);
        Image imaj = new Image(file.toURI().toString());

        //Image imaj = new Image("File:resources/assOrigins.jpg");
        ctrl.cutImage(imaj, n);
        ctrl.initPieces();

        return ctrl.initGrid();
    }

}
