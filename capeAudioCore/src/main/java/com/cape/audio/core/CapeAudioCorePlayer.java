/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cape.audio.core;

import java.net.URI;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;



/**
 *
 * @author ludetc
 */
public class CapeAudioCorePlayer extends Application {
        
    private final Label fileNameLabel = new Label("File name goes here");
    private final HBox spectrumBox = new HBox();
    
    AudioAdapter audioAdapter = new AudioAdapter();
    
    public void play(String loc) {
                       
            Media media = new Media(loc);
            
            MediaPlayer mplayer = new MediaPlayer(media);
            mplayer.play();
                        
    }
    
    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Cape Audio Player");
        
        final BorderPane mainBorderPane = new BorderPane();
        mainBorderPane.setTop(fileNameLabel);
        mainBorderPane.setCenter(spectrumBox);
        
        spectrumBox.setAlignment(Pos.CENTER);
        spectrumBox.setId("spectrum");
       
        VBox root = new VBox();        
        Scene scene = new Scene(root, 300, 275);
        root.getChildren().addAll(getMenuBar(), mainBorderPane);

        scene.getStylesheets().add("myStyles.css");
        
        primaryStage.setScene(scene);
        primaryStage.show();
 
        OverallSizeChangeListener changeListener = new OverallSizeChangeListener();
        
        primaryStage.heightProperty().addListener(changeListener);
        primaryStage.widthProperty().addListener(changeListener);
       
        redrawSpectrum();
        
        
    }
    
    private class OverallSizeChangeListener implements ChangeListener {
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {

            }
            
    }
    
    private void redrawSpectrum() {
        ArrayList<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < spectrumBox.getWidth(); i++) {
            int height = (int)(Math.random() * 50);
            Line line = new Line(i, 100-height, i, 100+height);
            line.setStroke(Color.RED);
            lines.add(line);
        }
        spectrumBox.getChildren().addAll(lines);
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
             
              URI uri = fileChooser.showOpenDialog(null).toURI();
             
              fileNameLabel.setText(uri.toString());
              
              play(uri.toString());
          }
      });
        
        menuFile.getItems().addAll(open);

        return menuBar;
    }
    
    
    
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
