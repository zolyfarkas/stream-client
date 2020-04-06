package org.spf4j.stream.client;


import java.io.IOException;
import org.openimaj.video.capture.VideoCaptureException;

/**
 * OpenIMAJ Hello world!
 *
 */
public class App {

  public static void main(String[] args) throws VideoCaptureException, IOException, InterruptedException {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionDisplayer());
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        Streamer streamer = new Streamer();
        streamer.setVisible(true);
      }
    });

  }








}
