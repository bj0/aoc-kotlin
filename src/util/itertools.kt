package util

import kotlin.math.max
import kotlin.math.min

fun <T> Sequence<T>.repeat() = sequence { while (true) yieldAll(this@repeat) }
fun <T> Sequence<T>.repeat(count: Int) = sequence { repeat(count) { yieldAll(this@repeat) } }

fun <T, K> Grouping<T, K>.eachMax(selector: (T) -> Int): Map<K, Int> = fold(0) { acc, e -> max(acc, selector(e)) }
fun <T, K> Grouping<T, K>.eachMin(selector: (T) -> Int): Map<K, Int> = fold(0) { acc, e -> min(acc, selector(e)) }
fun <T : Comparable<T>, K> Grouping<T, K>.eachMax(): Map<K, T> = reduce { _, acc, e -> maxOf(acc, e) }
fun <T : Comparable<T>, K> Grouping<T, K>.eachMin(): Map<K, T> = reduce { _, acc, e -> minOf(acc, e) }

/**
 * Make an [Sequence] returning elements from the iterable and saving a copy of each.
 * When the iterable is exhausted, return elements from the saved copy. Repeats indefinitely.
 *
 */
fun <T : Any> Iterable<T>.cycle(): Sequence<T> = sequence {
    val saved = mutableListOf<T>()
    for (elem in this@cycle) {
        saved.add(elem)
        yield(elem)
    }
    while (true) {
        for (elem in saved) yield(elem)
    }
}

/**
 * Make an [Sequence] that returns evenly spaced values starting with n
 *
 * @param start The value at which the sequence starts
 * @param step The step size
 */
fun count(start: Int = 0, step: Int = 1): Sequence<Int> = generateSequence(start) { it + step }

/**
 * Make an [Sequence] that returns evenly spaced values starting with n
 *
 * @param start The value at which the sequence starts
 * @param step The step size
 */
fun count(start: Long = 0, step: Long = 1): Sequence<Long> = generateSequence(start) { it + step }

/**
 * Make an [Sequence] that returns evenly spaced values starting with n
 *
 * @param start The value at which the sequence starts
 * @param step The step size
 */
fun count(start: Float = 0f, step: Float = 1f): Sequence<Float> = generateSequence(start) { it + step }

/**
 * Make an [Sequence] that returns evenly spaced values starting with n
 *
 * @param start The value at which the sequence starts
 * @param step The step size
 */
fun count(start: Double = 0.0, step: Double = 1.0): Sequence<Double> = generateSequence(start) { it + step }