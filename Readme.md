# Xoroshiro128+ for Java
The goal of this project was to create an atomic (thread-safe) Java version of the Xoroshiro128+ random number generator
(RNG).  The initial Java port was created by Tommy Ettinger for his [SquidPony/SquidLib project](https://github.com/SquidPony/SquidLib), and 
I've since went on to make it atomic and thread-safe for large scale distributed computing needs.

### Example Usage
Example with a random seed:
```java
final Xoroshiro128Plus rng = new Xoroshiro128Plus();
final int randomInt = rng.nextInt();
```

Example specifying a seed:
```java
final Xoroshiro128Plus rng = new Xoroshiro128Plus(123456);
final int randomInt = rng.nextLong();
```


### License
This is licensed as Creative Commons Attribution 4.0 International (CC BY 4.0):
https://creativecommons.org/licenses/by/4.0/

Feel free to use it in your own project or modify as necessary.

### Credit
 - Tommy Ettinger - The author of the original Java port for SquidPony's SquidLib: 
    https://raw.githubusercontent.com/SquidPony/SquidLib/master/squidlib-util/src/main/java/squidpony/squidmath/XoRoRNG.java
 - Sebastiano Vigna and David Blackman, which created the underlying algorithm: 
    http://xoroshiro.di.unimi.it/xoroshiro128plus.c

