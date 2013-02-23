/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cape.audio.core;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javazoom.jl.decoder.BitstreamException;

/**
 *
 * @author ludetc
 */
public class CapeAudioCorePlayer extends Application {

    private static final String titleHead = "Cape Audio ";
    private final AudioAdapter audioAdapter = new AudioAdapter();
    private AudioInputStream convertedAudioInputStream = null;
    private final WaveformBox waveBox = new WaveformBox();
    private Stage primaryStage = null;
    private MediaPlayer mediaPlayer = null;
    private WaveService waveService = new WaveService();


    public HBox getMediaBar() {
        
        final EventHandler<ActionEvent> backAction = new EventHandler<ActionEvent>() {
            @Override
                public void handle(ActionEvent e) {
                    mediaPlayer.seek(Duration.ZERO);
                }
            };
            final EventHandler<ActionEvent> stopAction = new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    mediaPlayer.stop();
                }
            };
            final EventHandler<ActionEvent> playAction = new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    mediaPlayer.play();
                }
            };
            final EventHandler<ActionEvent> pauseAction = new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    mediaPlayer.pause();
                }
            };
            final EventHandler<ActionEvent> forwardAction = new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    Duration currentTime = mediaPlayer.getCurrentTime();
                    mediaPlayer.seek(Duration.seconds(currentTime.toSeconds() + 0.1));
                }
            };
        
                    HBox mediaBar = HBoxBuilder.create()
                    .id("bottom")
                    .spacing(0)
                    .alignment(Pos.CENTER)
                    .children(
                        ButtonBuilder.create()
                            .id("back-button")
                            .text("Back")
                            .onAction(backAction)
                            .build(),
                        ButtonBuilder.create()
                            .id("stop-button")
                            .text("Stop")
                            .onAction(stopAction)
                            .build(),
                        ButtonBuilder.create()
                            .id("play-button")
                            .text("Play")
                            .onAction(playAction)
                            .build(),
                        ButtonBuilder.create()
                            .id("pause-button")
                            .text("Pause")
                            .onAction(pauseAction)
                            .build(),
                        ButtonBuilder.create()
                            .id("forward-button")
                            .text("Forward")
                            .onAction(forwardAction)
                            .build()
                     )
                    .build();
                    return mediaBar;
    }
    
    @Override
    public void start(final Stage primaryStage) {
        this.primaryStage = primaryStage;

        primaryStage.setTitle("Cape Audio Player");

        final BorderPane mainBorderPane = new BorderPane();
        mainBorderPane.setTop(getMediaBar());
        mainBorderPane.setCenter(waveBox);

        VBox root = new VBox();
        Scene scene = new Scene(root, 800, 500);
        root.getChildren().addAll(getMenuBar(), mainBorderPane);

        scene.getStylesheets().add("myStyles.css");

        primaryStage.setScene(scene);
        primaryStage.show();

        OverallSizeChangeListener changeListener = new OverallSizeChangeListener();

        primaryStage.heightProperty().addListener(changeListener);
        primaryStage.widthProperty().addListener(changeListener);
        
        waveService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                waveBox.redraw();
            }
        
        
        });
        

    }

    private class OverallSizeChangeListener implements ChangeListener {
        @Override
        public void changed(ObservableValue ov, Object t, Object t1) {
                redrawSpectrum();
        }
    }

    private class WaveService extends Service<String> {

        @Override
        protected Task<String> createTask() {
        return new Task<String>() {

                @Override
                protected String call() throws Exception {
                    waveBox.generateWave();
                    return "Ok";
                }
            
        };
       }
        
        
    }
    private void redrawSpectrum() {
            waveService.start();
    }

    public MenuBar getMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu menuFile = new Menu("File");

        menuBar.getMenus().addAll(menuFile);

        MenuItem open = new MenuItem("Open");
        open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();

                //Set extension filter
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP3 files (*.mp3)", "*.mp3");
                fileChooser.getExtensionFilters().add(extFilter);

                File selectedFile = fileChooser.showOpenDialog(null);
                URI uri = selectedFile.toURI();

                try {
                    audioAdapter.load(selectedFile);
                    convertedAudioInputStream = audioAdapter.getConvertedAudioInputStream();
                    waveBox.setAudioAdapter(audioAdapter);
                    primaryStage.setTitle(titleHead + selectedFile.getName());
                    redrawSpectrum();
                } catch (UnsupportedAudioFileException | IOException | BitstreamException ex) {
                    Logger.getLogger(CapeAudioCorePlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                Media m = new Media(uri.toString());
                mediaPlayer = new MediaPlayer(m);
            }
        });

        menuFile.getItems().addAll(open);

        return menuBar;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
