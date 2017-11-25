/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package garrett.benjamin.arora_tsp_path_viewer;

import java.awt.geom.Point2D;

/**
 *
 * @author benjamin
 */
public class PathPoint2D extends Point2D {

  public static final int DATA_POINT = 1;
  public static final int PORTAL_POINT = 0;
  public static final int PARTITION = 2;
  private double x, y;
  private int type;

  public PathPoint2D(int type) {
    this.type = type;
  }

  public int getType() {
    return type;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public void setLocation(double x, double y) {
    this.x = x;
    this.y = y;
  }

}
