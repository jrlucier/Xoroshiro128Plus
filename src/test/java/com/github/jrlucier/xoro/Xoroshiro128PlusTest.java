package com.github.jrlucier.xoro;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
}
