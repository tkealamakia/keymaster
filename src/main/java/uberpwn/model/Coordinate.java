package uberpwn.model;

import java.io.Serializable;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class Coordinate implements Serializable {
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  private Double x;
  private Double y;

  public Coordinate() {

  }

  public Coordinate(Double x, Double y) {
    this.x = x;
    this.y = y;
  }

  public Double getX() {
    return x;
  }

  public void setX(Double x) {
    this.x = x;
  }

  public Double getY() {
    return y;
  }

  public void setY(Double y) {
    this.y = y;
  }

  @Override
  public boolean equals(Object o) {
    return Pojomatic.equals(this, o);
  }

  @Override
  public int hashCode() {
    return Pojomatic.hashCode(this);
  }

  @Override
  public String toString() {
    return Pojomatic.toString(this);
  }

}
