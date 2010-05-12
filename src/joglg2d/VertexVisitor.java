package joglg2d;

/**
 * @author borkholder
 * @created May 11, 2010
 *
 */
public interface VertexVisitor {
  void moveTo(double[] vertex);

  void lineTo(double[] vertex);

  void closeLine();

  void beginPoly(int windingRule);

  void endPoly();
}
