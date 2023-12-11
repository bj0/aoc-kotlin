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

data class Point<T : Number>(val x: T, val y: T)

infix fun <T : Number> Point<T>.mdist(other: Point<T>) = (other.x - x).absoluteValue + (other.y - y).absoluteValue

@Suppress("UNCHECKED_CAST")
private operator fun <T : Number> T.plus(x: T): T = when (this) {
    is Int -> (this + (x as Int)) as T
    is Long -> (this + (x as Long)) as T
    is Double -> (this + (x as Double)) as T
    is Float -> (this + (x as Float)) as T
    else -> throw IllegalArgumentException("unsupported type")
}

@Suppress("UNCHECKED_CAST")
private operator fun <T : Number> T.minus(x: T): T = when (this) {
    is Int -> (this - (x as Int)) as T
    is Long -> (this - (x as Long)) as T
    is Double -> (this - (x as Double)) as T
    is Float -> (this - (x as Float)) as T
    else -> throw IllegalArgumentException("unsupported type")
}

@Suppress("UNCHECKED_CAST")
val <T : Number> T.absoluteValue: T
    get() = when (this) {
        is Int -> absoluteValue as T
        is Long -> absoluteValue as T
        is Double -> absoluteValue as T
        is Float -> absoluteValue as T
        else -> throw IllegalArgumentException("unsupported type")
    }

data class PointL(val x: Long, val y: Long)

infix fun PointL.mdist(other: PointL) = (other.x - x).absoluteValue + (other.y - y).absoluteValue

fun main() {
    testLcm()
}