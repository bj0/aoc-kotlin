package util

import java.math.BigInteger
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.time.measureTime


/**
 * recursive gcd
 */
tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

/**
 * recursive lcm
 */
fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b

fun List<Long>.lcm() = reduce { acc, i -> lcm(acc, i) }

/**
 * chinese remainder theorem for 2 congruences
 * N == x (mod n)
 * N == a (mod m)
 */
tailrec fun crt(x: Long, n: Long, a: Long, m: Long): Long = if (x % m == a % m) x else crt(x + n, n, a % m, m)

/**
 * chinese remainder theorem, returns (N, lcm)
 */
fun Iterable<Pair<Long, Long>>.crt() = reduce { (x, n), (a, m) -> crt(x, n, a, m) to lcm(m, n) }

fun Iterable<Int>.product() = fold(1) { acc, i -> acc * i }

fun testLcm() {
    fun lcmMutable(a: Long, b: Long): Long {
        val larger = max(a, b)
        val smaller = min(a, b)
        val maxLcm = a * b
        var lcm = larger
        while (lcm <= maxLcm) {
            if (lcm % smaller == 0L) {
                return lcm
            }
            lcm += larger
        }
        return maxLcm
    }

    // almost twice as long
    fun lcmSequence(a: Long, b: Long): Long {
        val larger = max(a, b)
        val smaller = min(a, b)
        val maxLcm = a * b
        return generateSequence(larger) { l -> l + larger }
            .takeWhile { l -> l <= maxLcm }
            .first { l -> l % smaller == 0L }
    }

    fun lcmBig(a: Long, b: Long): Long {
        return a / BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).toLong() * b
    }

    val n = 1000
//    val (a, b) = 3974592L to 257575L
    val (a, b) = 257575L to 3974592L
    check(setOf(lcmMutable(a, b), lcmSequence(a, b), lcm(a, b)).size == 1)
    measureTime { repeat(n) { lcmMutable(a, b) } }.println()
    measureTime { repeat(n) { lcmSequence(a, b) } }.println() // slowest
    measureTime { repeat(n) { lcm(a, b) } }.println() // fastest
    measureTime { repeat(n) { lcmBig(a, b) } }.println()
}

fun main() {
//    testLcm()

    listOf(
        2767L to 3767L,
        2779L to 3779L
    ).crt().debug("crt:")
}