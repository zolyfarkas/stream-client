
package org.spf4j.stream.client;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import io.humble.video.Codec;
import io.humble.video.Encoder;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.Muxer;
import io.humble.video.MuxerFormat;
import io.humble.video.PixelFormat;
import io.humble.video.Rational;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.JPanel;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.image.processor.SinglebandImageProcessor;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.capture.Device;
import org.openimaj.video.capture.VideoCapture;
import org.openimaj.video.capture.VideoCaptureException;

/**
 *
 * @author Zoltan Farkas
 */
public class DefaultCameraStreamer implements Closeable {

  private final VideoDisplay<MBFImage> display;

  DefaultCameraStreamer(final JPanel playPanel,
          final String publishUrl, Resolution res) throws VideoCaptureException, IOException, InterruptedException {
    playPanel.paint(playPanel.getGraphics());
    List<Device> videoDevices = VideoCapture.getVideoDevices();
    System.out.println(videoDevices);
    Rational framerate = Rational.make(1, 25);
    int width = res.getWidth();
    int height = res.getHeight();
    Device firstOne = videoDevices.get(0);
    VideoCapture video = new VideoCapture(width, height, 1d / framerate.getDouble(), firstOne);
    this.display = VideoDisplay.createVideoDisplay(video, playPanel);
    display.addVideoListener(new FilteringHlsStreamingListener(publishUrl, framerate,
            width, height));
  }

  public void play() {
    display.setMode(VideoDisplay.Mode.PLAY);
  }

  public void stop() {
    display.setMode(VideoDisplay.Mode.STOP);
  }

  @Override
  public void close() throws IOException {
    display.close();
  }

  private static class FilteringHlsStreamingListener implements VideoDisplayListener<MBFImage>, Closeable {

    private final Encoder encoder;
    private final MediaPacket packet;
    private final Muxer muxer;
    private MediaPictureConverter converter;
    private BufferedImage f;
    private final long videoEpoch = System.nanoTime();
    private final SinglebandImageProcessor<Float, FImage> processor;
    private final Disruptor<MediaPicture> disruptor;
    private final EventFactory<MediaPicture> picFactory;


    public FilteringHlsStreamingListener(String publishUrl, Rational framerate,
            int width, int height) throws IOException, InterruptedException {
      this.muxer = Muxer.make(publishUrl, null, "hls");
      muxer.setProperty("start_number", 0);
      muxer.setProperty("hls_time", 2);
      muxer.setProperty("hls_list_size", 20);
      muxer.setProperty("hls_wrap", 20);
      final MuxerFormat format = muxer.getFormat();
      Codec codec = Codec.findEncodingCodecByName("libx264");
      encoder = Encoder.make(codec);
      /**
       * Video encoders need to know at a minimum: width height pixel format Some also need to know frame-rate (older
       * codecs that had a fixed rate at which video files could be written needed this). There are many other options
       * you can set on an encoder, but we're going to keep it simpler here.
       */
      encoder.setWidth(width);
      encoder.setHeight(height);
      // We are going to use 420P as the format because that's what most video formats these days use
      final PixelFormat.Type pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
      encoder.setPixelFormat(pixelformat);
      Rational timeBase = Rational.make(1, 25);

      encoder.setTimeBase(timeBase);

      /**
       * An annoyance of some formats is that they need global (rather than per-stream) headers, and in that case you
       * have to tell the encoder. And since Encoders are decoupled from Muxers, there is no easy way to know this
       * beyond
       */
      if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER)) {
        encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
      }

      /**
       * Open the encoder.
       */
      encoder.open(null, null);

      /**
       * Add this stream to the muxer.
       */
      muxer.addNewStream(encoder);

      muxer.open(null, null);
      this.packet = MediaPacket.make();
      this.processor = new CannyEdgeDetector();
      this.picFactory = () -> {
        MediaPicture picture = MediaPicture.make(
                encoder.getWidth(),
                encoder.getHeight(),
                pixelformat);
        picture.setTimeBase(timeBase);
        return picture;
      };
      this.disruptor = new Disruptor<>(picFactory, 64, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE,
              new BlockingWaitStrategy());
      this.disruptor.handleEventsWith(new EventHandler<MediaPicture>(){
        @Override
        public void onEvent(MediaPicture picture, long arg1, boolean arg2) throws Exception {
          do {
            encoder.encode(packet, picture);
            if (packet.isComplete()) {
              muxer.write(packet, true);
            } else {
              break;
            }
          } while (true);
        }
      });
      this.disruptor.start();
    }

    public void beforeUpdate(MBFImage frame) {
      frame.processInplace(processor);
      if (converter == null) {
        f = ImageUtilities.createBufferedImageForDisplay(frame);
        converter = MediaPictureConverterFactory.createConverter(f, picFactory.newInstance());
      } else {
        f = ImageUtilities.createBufferedImageForDisplay(frame, f);
      }
      RingBuffer<MediaPicture> ringBuffer = disruptor.getRingBuffer();
      long sequence = ringBuffer.next();  // Grab the next sequence
      try {
        MediaPicture picture = ringBuffer.get(sequence);
        converter.toPicture(picture, f, (System.nanoTime() - videoEpoch) * 25 / 1000000000);
      } finally {
        ringBuffer.publish(sequence);
      }
    }

    public void afterUpdate(VideoDisplay<MBFImage> display) {
    }

    @Override
    public void close() throws IOException {
      try {
        disruptor.shutdown(10, TimeUnit.SECONDS);
      } catch (TimeoutException ex) {
        disruptor.halt();
      }
      try {
        do {
          encoder.encode(packet, null);
          if (packet.isComplete()) {
            muxer.write(packet, false);
          } else {
            break;
          }
        } while (true);
      } finally {
        muxer.close();
      }
    }
  }

}
