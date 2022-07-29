package io.github.jrlucier.xoro;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class Xoroshiro128PlusTest {

  @Test
  public void testRng() {
    final Xoroshiro128Plus x128p = new Xoroshiro128Plus(123, 456);

    assertEquals("Should match", 579, x128p.nextLong());
    assertEquals("Should match", 4431571926312075699L, x128p.nextLong());
    assertEquals("Should match", -1612580728535389485L, x128p.nextLong());
    assertEquals("Should match", 4468099366319113814L, x128p.nextLong());
    assertEquals("Should match", 167286530559998105L, x128p.nextLong());
  }

  @Test
  public void testEmptyConstructorState() {
    final Xoroshiro128Plus x1 = new Xoroshiro128Plus();
    final Xoroshiro128Plus x2 = new Xoroshiro128Plus();

    assertNotEquals(x1.getStateA(), x2.getStateA());
    assertNotEquals(x1.getStateB(), x2.getStateB());
  }

  @Test
  public void testEmptyConstructorStateSingleSeed() {
    final Xoroshiro128Plus x1 = new Xoroshiro128Plus(123);

    assertEquals("Should match", -5414281315512073941L, x1.getStateA());
    assertEquals("Should match", -431715638815246468L, x1.getStateB());
  }
}
