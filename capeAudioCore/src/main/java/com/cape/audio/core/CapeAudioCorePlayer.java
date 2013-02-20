/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cape.audio.core;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;



/**
 *
 * @author ludetc
 */
public class CapeAudioCorePlayer extends Application {
    
    private String fileURL = "file:///home/ludetc/Downloads/roberto.mp3";
    
    private final Label fileNameLabel = new Label();
    
    public void play(String loc) {
                       
            Media media = new Media(loc);
            
            MediaPlayer mplayer = new MediaPlayer(media);
            mplayer.play();
                        
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cape Audio Player");
                

        
        final VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(0, 10, 0, 10));
        vbox.getChildren().addAll(fileNameLabel);

        Scene scene = new Scene(new VBox(), 300, 275);
        ((VBox) scene.getRoot()).getChildren().addAll(getMenuBar(), vbox);

        
        primaryStage.setScene(scene);
        primaryStage.show();
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
             
              //Show open file dialog
              fileURL = "file://" + fileChooser.showOpenDialog(null).getPath();
             
              fileNameLabel.setText(fileURL);
              
              play(fileURL);
          }
      });
        
        menuFile.getItems().addAll(open);

        return menuBar;
    }
    
    
    
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
