package io.github.jrlucier.xoro;

import java.io.Serializable;
import java.util.Objects;

/**
 * Keep track of the state together as a single object for compare and set operations.
 *
 * @author Jeremy Lucier
 */
public class Xoroshiro128PlusState implements Serializable {

  private static final long serialVersionUID = 1018744536171610222L;

  private final long state0;
  private final long state1;

  public Xoroshiro128PlusState(long state0, long state1) {
    this.state0 = state0;
    this.state1 = state1;
  }

  public long getState0() {
    return state0;
  }

  public long getState1() {
    return state1;
  }

  @Override
  public String toString() {
    return "Xoroshiro128PlusState{" + "state0=" + state0 + ", state1=" + state1 + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Xoroshiro128PlusState that = (Xoroshiro128PlusState) o;
    return state0 == that.state0 && state1 == that.state1;
  }

  @Override
  public int hashCode() {
    return Objects.hash(state0, state1);
  }
}
