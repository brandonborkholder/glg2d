package org.jogamp.glg2d.util;

import java.util.LinkedList;
import java.util.Queue;

public class Timer {
  private static final Timer instance = new Timer();

  private long started;

  private Queue<Long> times = new LinkedList<Long>();

  public static Timer getInstance() {
    return instance;
  }

  public void start() {
    started = System.nanoTime();
  }

  public void stop() {
    times.add(System.nanoTime() - started);

    if (times.size() > 10) {
      times.poll();
    }
  }

  public void stopAndPrint() {
    stop();

    double total = 0;
    for (Long val : times) {
      total += val;
    }

    System.out.println(String.format("Moving avg: %.3f ms", total / 1e6));
  }
}
