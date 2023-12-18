package util

import java.util.*

inline fun <T, C : Comparable<C>> dijkstraC(
    start: T,
    isEnd: (T) -> Boolean,
    crossinline cost: (T) -> C,
    crossinline choices: suspend SequenceScope<T>.(T) -> Unit
): T {
    val bests = mutableMapOf<T, C>()
    val pending = PriorityQueue<T>(compareBy { cost(it) })
    pending += start
    while (pending.isNotEmpty()) {
        val current = pending.poll()
        if (isEnd(current)) return current
        val opts = sequence { choices(current) }.toList()
        for (next in opts) {
            val c = cost(next)
            if (next !in bests || bests[next]!! > c) {
                bests[next] = c
                pending += next
            }
        }
    }
    error("no route")
}

inline fun <T> dijkstra(
    start: T,
    initialCost: Int = 0,
    isEnd: (v: T) -> Boolean,
    crossinline cost: (u: T, v: T) -> Int,
    crossinline choices: suspend SequenceScope<T>.(T) -> Unit
): Pair<List<T>, Int> {
    val dist = mutableMapOf(start to initialCost)
    val prev = mutableMapOf<T, T>()
    val pending = PriorityQueue<T>(compareBy { dist[it]!! })
    pending += start
    while (pending.isNotEmpty()) {
        val current = pending.remove()
        if (isEnd(current)) return generateSequence(current) { prev[it] }.toList().reversed() to dist[current]!!
        val opts = sequence { choices(current) }.toList()
        for (next in opts) {
            val c = dist[current]!! + cost(current, next)
            if (next !in dist || dist[next]!! > c) {
                dist[next] = c
                prev[next] = current
                pending += next
            }
        }
    }
    error("no route")
}