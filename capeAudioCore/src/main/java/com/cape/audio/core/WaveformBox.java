/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cape.audio.core;

import java.io.IOException;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author ludetc
 */
public class WaveformBox extends HBox {
    
      private AudioAdapter audioAdapter;

      private volatile boolean canceled = false;

      private boolean userHasDragged;
  private volatile boolean isLoading;
  private int loopStart;
  private int loopEnd;
  private long framesPerPixel;
    private int numFramesRead;


  
  public WaveformBox() {
      this.setAlignment(Pos.CENTER);
  }
  
  public void setAudioAdapter(AudioAdapter adapter) {
      this.audioAdapter = adapter;
  }
    
  public void redraw() throws UnsupportedAudioFileException, IOException {
      getChildren().clear();;
      
    userHasDragged = false;
    isLoading = true;
    loopStart = -1;
    loopEnd = -1;

    AudioInputStream convertedAudioInputStream = audioAdapter.getConvertedAudioInputStream();

    double w = getWidth();
    double h = getHeight();
    h = 300;
    byte[] buffer = new byte[4096];
    long songLengthInFrames = audioAdapter.getSongLength();
    this.framesPerPixel = (long)(songLengthInFrames / w);
    int numBytesRead = 0;
    this.numFramesRead = 0;
    
    float maxL = 0.0F; 
    float maxR = 0.0F; 
    float minL = 0.0F; 
    float minR = 0.0F;
    
    int frame = 0;
    
    float x = 0.0F;

    while ((!this.canceled) && ((numBytesRead = convertedAudioInputStream.read(buffer, 0, buffer.length)) != -1)) {
      for (int i = 0; i < numBytesRead && !canceled; i += 4) {
        float left = (buffer[(i + 1)] << 8 | buffer[i] & 0xFF) / 65536.0F;
        float right = (buffer[(i + 3)] << 8 | buffer[(i + 2)] & 0xFF) / 65536.0F;
        if (left > maxL) {
          maxL = left;
        }
        if (right > maxR) {
          maxR = right;
        }
        if (left < minL) {
          minL = left;
        }
        if (right < minR) {
          minR = right;
        }

        
        frame++;
        if (frame > framesPerPixel) {
          Line line = new Line(x, minL * (h / 2) + h / 2 / 2.0F, x, maxL * (h / 2) + h / 2 / 2.0F);

//          line.y1 = (maxR * (h / 2) + h / 2 / 2.0F + h / 2);
//          line.y2 = (minR * (h / 2) + h / 2 / 2.0F + h / 2);

          getChildren().addAll(line);
          
          maxL = maxR = minL = minR = 0.0F;
          frame = 0;
          x ++;
        }
        this.numFramesRead += 1;
      }
    }

        if (this.canceled) {
            System.out.println("Canceled!");
        }
    this.canceled = false;
    convertedAudioInputStream.close();
    this.isLoading = false;
  }
    
}
