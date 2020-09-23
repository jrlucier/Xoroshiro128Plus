package com.github.jrlucier.rng;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A thread-safe Xoroshiro128+ Java Implementation.
 *
 * <p>This is licensed as Creative Commons Attribution 4.0 International (CC BY 4.0):
 * https://creativecommons.org/licenses/by/4.0/
 *
 * @author Jeremy Lucier (developer of this implementation)
 */
public class Xoroshiro128Plus implements Serializable {
  private static final long DOUBLE_MASK = (1L << 53) - 1;
  private static final double NORM_53 = 1. / (1L << 53);
  private static final long FLOAT_MASK = (1L << 24) - 1;
  private static final double NORM_24 = 1. / (1L << 24);

  private static final long serialVersionUID = 1018744536171610222L;

  private final AtomicReference<Xoroshiro128PlusState> state = new AtomicReference<>();

  /** Creates a new generator seeded using four calls to Math.random(). */
  public Xoroshiro128Plus() {
    this(
        (long) ((Math.random() - 0.5) * 0x10000000000000L)
            ^ (long) (((Math.random() - 0.5) * 2.0) * 0x8000000000000000L),
        (long) ((Math.random() - 0.5) * 0x10000000000000L)
            ^ (long) (((Math.random() - 0.5) * 2.0) * 0x8000000000000000L));
  }

  /**
   * Constructs this XoRoRNG by dispersing the bits of seed using {@link #setSeed(long)} across the
   * two parts of state this has.
   *
   * @param seed a long that won't be used exactly, but will affect both components of state
   */
  public Xoroshiro128Plus(final long seed) {
    setSeed(seed);
  }

  /**
   * Constructs this XoRoRNG by calling {@link #setSeed(long, long)} on the arguments as given; see
   * that method for the specific details (stateA and stateB are kept as-is unless they are both 0).
   *
   * @param stateA the number to use as the first part of the state; this will be 1 instead if both
   *     seeds are 0
   * @param stateB the number to use as the second part of the state
   */
  Xoroshiro128Plus(final long stateA, final long stateB) {
    setSeed(stateA, stateB);
  }

  public final int next(int bits) {
    for (;;) {
      final Xoroshiro128PlusState curState = this.state.get();
      final long s0 = curState.getState0();
      final long s1 = curState.getState1() ^ s0;

      final Xoroshiro128PlusState nextState =
          new Xoroshiro128PlusState(
              (s0 << 55 | s0 >>> 9) ^ s1 ^ (s1 << 14), // a, b
              (s1 << 36 | s1 >>> 28)); // c

      if (this.state.compareAndSet(curState, nextState)) {
        return (int) (curState.getState0() + curState.getState1()) >>> (32 - bits);
      }
    }
  }

  public final long nextLong() {
    for(;;) {
      final Xoroshiro128PlusState curState = this.state.get();
      final long s0 = curState.getState0();
      final long s1 = curState.getState1() ^ s0;

      final Xoroshiro128PlusState nextState =
          new Xoroshiro128PlusState(
              (s0 << 55 | s0 >>> 9) ^ s1 ^ (s1 << 14), // a, b
              (s1 << 36 | s1 >>> 28)); // c

      if (this.state.compareAndSet(curState, nextState)) {
        return curState.getState0() + curState.getState1();
      }
    }
  }

  /**
   * Produces a copy of this RandomnessSource that, if next() and/or nextLong() are called on this
   * object and the copy, both will generate the same sequence of random numbers from the point
   * copy() was called. This just needs to copy the state so it isn't shared, usually, and produce a
   * new value with the same exact state.
   *
   * @return a copy of this RandomnessSource
   */
  public Xoroshiro128Plus copy() {
    final Xoroshiro128PlusState st = this.state.get();
    return new Xoroshiro128Plus(st.getState0(), st.getState1());
  }

  /**
   * Can return any int, positive or negative, of any size permissible in a 32-bit signed integer.
   *
   * @return any int, all 32 bits are random
   */
  public int nextInt() {
    return (int) nextLong();
  }

  /**
   * Exclusive on the outer bound; the inner bound is 0. The bound may be negative, which will
   * produce a non-positive result.
   *
   * @param exclusiveOuterBound the outer exclusive bound; may be positive or negative
   * @return a random int between 0 (inclusive) and bound (exclusive)
   */
  public int nextInt(final int exclusiveOuterBound) {
    return (int) ((exclusiveOuterBound * (nextLong() >>> 33)) >> 31);
  }

  /**
   * Inclusive lower, exclusive upper.
   *
   * @param inclusiveInnerBound the inner bound, inclusive, can be positive or negative
   * @param exclusiveOuterBound the outer bound, exclusive, should be positive, should usually be
   *     greater than inner
   * @return a random int that may be equal to inner and will otherwise be between inner and outer
   */
  public int nextInt(final int inclusiveInnerBound, final int exclusiveOuterBound) {
    return inclusiveInnerBound + nextInt(exclusiveOuterBound - inclusiveInnerBound);
  }

  /**
   * Exclusive on the outer exclusiveOuterBound; the inner exclusiveOuterBound is 0. The
   * exclusiveOuterBound may be negative, which will produce a non-positive result.
   *
   * @param exclusiveOuterBound the outer exclusive exclusiveOuterBound; may be positive or negative
   * @return a random long between 0 (inclusive) and exclusiveOuterBound (exclusive)
   */
  public long nextLong(long exclusiveOuterBound) {
    long rand = nextLong();
    final long randLow = rand & 0xFFFFFFFFL;
    final long boundLow = exclusiveOuterBound & 0xFFFFFFFFL;

    // Shift
    final long randShifted = rand >>> 32;
    final long eOuterBandShifted = exclusiveOuterBound >> 32;

    final long t = randShifted * boundLow + (randLow * boundLow >>> 32);
    return randShifted * eOuterBandShifted
        + (t >> 32)
        + (randLow * eOuterBandShifted + (t & 0xFFFFFFFFL) >> 32);
  }

  /**
   * Inclusive inner, exclusive outer; both inner and outer can be positive or negative.
   *
   * @param inclusiveInnerBound the inner bound, inclusive, can be positive or negative
   * @param exclusiveOuterBound the outer bound, exclusive, can be positive or negative and may be
   *     greater than or less than inner
   * @return a random long that may be equal to inner and will otherwise be between inner and outer
   */
  public long nextLong(final long inclusiveInnerBound, final long exclusiveOuterBound) {
    return inclusiveInnerBound + nextLong(exclusiveOuterBound - inclusiveInnerBound);
  }

  public double nextDouble() {
    return (nextLong() & DOUBLE_MASK) * NORM_53;
  }

  public float nextFloat() {
    return (float) ((nextLong() & FLOAT_MASK) * NORM_24);
  }

  public boolean nextBoolean() {
    return nextLong() < 0L;
  }

  public void nextBytes(final byte[] bytes) {
    int i = bytes.length;
    int n;
    while (i != 0) {
      n = Math.min(i, 8);
      for (long bits = nextLong(); n-- != 0; bits >>>= 8) {
        bytes[--i] = (byte) bits;
      }
    }
  }

  /**
   * Sets the seed of this generator using one long, running that through LightRNG's algorithm twice
   * to get the state.
   *
   * @param seed the number to use as the seed
   */
  private void setSeed(final long seed) {

    long state = seed + 0x9E3779B97F4A7C15L;
    long z = state;
    z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
    z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
    final long state0 = z ^ (z >>> 31);

    state += 0x9E3779B97F4A7C15L;
    z = state;
    z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
    z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
    final long state1 = z ^ (z >>> 31);

    this.state.set(new Xoroshiro128PlusState(state0, state1));
  }

  /**
   * Sets the seed of this generator using two longs, using them without changes unless both are 0
   * (then it makes the state variable corresponding to stateA 1 instead).
   *
   * @param stateA the number to use as the first part of the state; this will be 1 instead if both
   *     seeds are 0
   * @param stateB the number to use as the second part of the state
   */
  private void setSeed(final long stateA, final long stateB) {
    this.state.set(new Xoroshiro128PlusState(((stateA | stateB) == 0L) ? 1L : stateA, stateB));
  }

  /**
   * Gets the first component of this generator's two-part state, as a long. This can be 0 on its
   * own, but will never be 0 at the same time as the other component of state, {@link
   * #getStateB()}. You can set the state with two exact values using {@link #setSeed(long, long)},
   * but the alternative overload {@link #setSeed(long)} won't use the state without changing it (it
   * needs to cover 128 bits with a 64-bit value).
   *
   * @return the first component of this generator's state
   */
  public long getStateA() {
    return this.state.get().getState0();
  }

  /**
   * Gets the second component of this generator's two-part state, as a long. This can be 0 on its
   * own, but will never be 0 at the same time as the other component of state, {@link
   * #getStateA()}. You can set the state with two exact values using {@link #setSeed(long, long)},
   * but the alternative overload {@link #setSeed(long)} won't use the state without changing it (it
   * needs to cover 128 bits with a 64-bit value).
   *
   * @return the second component of this generator's state
   */
  public long getStateB() {
    return this.state.get().getState1();
  }

  @Override
  public String toString() {
    return "Xoroshiro128plus with stateA 0x"
        + Long.toHexString(this.state.get().getState0())
        + "L and stateB 0x"
        + Long.toHexString(this.state.get().getState1())
        + 'L';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Xoroshiro128Plus xoRoRNG = (Xoroshiro128Plus) o;

    if (this.state.get().getState0() != xoRoRNG.state.get().getState0()) return false;
    return this.state.get().getState1() == xoRoRNG.state.get().getState1();
  }

  @Override
  public int hashCode() {
    return (int)
        (31L * (this.state.get().getState0() ^ (this.state.get().getState0() >>> 32))
            + (this.state.get().getState1() ^ (this.state.get().getState1() >>> 32)));
  }
}
