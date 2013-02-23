/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cape.audio.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import org.tritonus.share.sampled.file.TAudioFileFormat;

public class AudioAdapter
{
  private AudioFileFormat sourceFileFormat;
  private AudioFormat targetFormat;
  private AudioInputStream convertedAudioInputStream;
  private volatile File file;
  private String title;
  private long songLengthInFrames;
  private ArrayList<HeaderTimeData> headerTimeData;
  private MpegAudioFileReader reader;

  public AudioAdapter()
  {
    targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F, false);
    reader = new MpegAudioFileReader();
  }

  public String getSongTitle() {
    return title;
  }

  public void load(File file) throws UnsupportedAudioFileException, IOException, BitstreamException {
    this.file = file;
    if (convertedAudioInputStream != null) {
      convertedAudioInputStream.close();
    }

    if (file.getName().toLowerCase().endsWith("mp3")) {
          sourceFileFormat = reader.getAudioFileFormat(file);
      }
    else {
      sourceFileFormat = AudioSystem.getAudioFileFormat(file);
    }
    convertedAudioInputStream = getConvertedAudioInputStream();

    if ((sourceFileFormat instanceof TAudioFileFormat)) {
      Map properties = ((TAudioFileFormat)this.sourceFileFormat).properties();
      long val = ((Long)properties.get("duration")).longValue();
      title = ((String)properties.get("title"));
      songLengthInFrames = ((long)(val * 0.0441D));
    } else {
      title = file.getName();
      songLengthInFrames = convertedAudioInputStream.getFrameLength();
    }

    this.headerTimeData = new ArrayList();

    if ((sourceFileFormat instanceof TAudioFileFormat)) {
      int ms = 0;
      Bitstream bitstream = new Bitstream(new FileInputStream(file));

      int bytesRead = 0;
      Header h;
      while ((h = bitstream.readFrame()) != null) {
        HeaderTimeData timeData = new HeaderTimeData();
        bytesRead += h.calculate_framesize();
        timeData.bytesAccumulated = bytesRead;
        ms = (int)(ms + h.ms_per_frame());
        HeaderTimeData tmp248_246 = timeData; 
        tmp248_246.framesAccumulated = ((int)(tmp248_246.framesAccumulated + ms * 44.100000000000001D));
        bitstream.closeFrame();
        this.headerTimeData.add(timeData);
      }
    }
  }

  public long getSongLength() {
    return this.songLengthInFrames;
  }

  public long getFramePositionInBytes(long frame) {
    if ((sourceFileFormat instanceof TAudioFileFormat)) {
          for (Iterator<HeaderTimeData> it = headerTimeData.iterator(); it.hasNext();) {
              HeaderTimeData h = it.next();
              if (h.framesAccumulated > frame) {
                return h.bytesAccumulated;
              }
          }
    }
    return frame * 4L;
  }

  public AudioInputStream getConvertedAudioInputStream() throws UnsupportedAudioFileException, IOException {
    if ((sourceFileFormat instanceof TAudioFileFormat)) {
      return AudioSystem.getAudioInputStream(targetFormat, reader.getAudioInputStream(file));
    }

    return AudioSystem.getAudioInputStream(targetFormat, AudioSystem.getAudioInputStream(file));
  }

  private class HeaderTimeData
  {
    public int bytesAccumulated;
    public int framesAccumulated;

    private HeaderTimeData()
    {
    }
  }
}