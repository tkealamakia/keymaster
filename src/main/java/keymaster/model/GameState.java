package keymaster.model;

import java.io.Serializable;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GameState implements Serializable {

  private static final long serialVersionUID = 1L;

  private Boolean addonFound;
  private Boolean addonCompatible;
  private Integer attack;

  public Boolean getAddonFound() {
    return addonFound;
  }

  public void setAddonFound(Boolean addonFound) {
    this.addonFound = addonFound;
  }

  public Boolean getAddonCompatible() {
    return addonCompatible;
  }

  public void setAddonCompatible(Boolean addonCompatible) {
    this.addonCompatible = addonCompatible;
  }

  public Integer getAttack() {
    return attack;
  }

  public void setAttack(Integer attack) {
    this.attack = attack;
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
