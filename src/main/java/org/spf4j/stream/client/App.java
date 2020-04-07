package org.spf4j.stream.client;


import java.io.IOException;
import org.openimaj.video.capture.VideoCaptureException;
import org.spf4j.stackmonitor.Sampler;

/**
 * OpenIMAJ Hello world!
 *
 */
public class App {

  public static void main(String[] args) throws VideoCaptureException, IOException, InterruptedException {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionDisplayer());
    Sampler sampler = new Sampler(10);
    sampler.registerJmx();
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        Streamer streamer = new Streamer();
        streamer.setVisible(true);
      }
    });

  }








}
