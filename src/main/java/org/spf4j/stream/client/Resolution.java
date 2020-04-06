
package org.spf4j.stream.client;

/**
 *
 * @author Zoltan Farkas
 */
public enum Resolution {

  Small(320, 240), Medium(640, 480), Large(1280, 960);

  private final int width;
  private final int height;

  Resolution(final int width, final int height) {
    this.width = width;
    this.height = height;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }



}
