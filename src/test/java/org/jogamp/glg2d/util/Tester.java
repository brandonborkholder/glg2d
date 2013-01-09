package org.jogamp.glg2d.util;

public interface Tester {
  void setPainter(Painter p);
  
  void assertSame() throws InterruptedException;
  
  void finish();
}
