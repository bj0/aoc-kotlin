package util

import java.util.*

inline fun <T, C : Comparable<C>> dijkstraAll(
    start: T,
    crossinline isEnd: (T) -> Boolean,
    crossinline cost: (T) -> C,
    crossinline choices: suspend SequenceScope<T>.(T) -> Unit
): Sequence<T> = sequence {
    val bests = mutableMapOf<T, C>()
    val pending = PriorityQueue<T>(compareBy { cost(it) })
    pending += start
    while (pending.isNotEmpty()) {
        val current = pending.poll()
        if (isEnd(current)) {
            yield(current)
            continue
        }
        val opts = sequence { choices(current) }.toList()
        for (next in opts) {
            val c = cost(next)
            if (next !in bests || bests[next]!! > c) {
                bests[next] = c
                pending += next
            }
        }
    }
}


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

// https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Using_a_priority_queue
inline fun <T> dijkstra2(
    start: T,
    initialCost: Int = 0,
    isEnd: (v: T) -> Boolean,
    cost: (u: T, v: T) -> Int,
    crossinline choices: suspend SequenceScope<T>.(T) -> Unit
): Pair<List<T>, Int> {
    val dist = mutableMapOf(start to initialCost)
    val prev = mutableMapOf<T, T>()
    val pending = PriorityQueue<T>(compareBy { dist[it]!! })
    pending += start
    while (pending.isNotEmpty()) {
        val current = pending.remove()

        if (isEnd(current)) return generateSequence(current) { prev[it] }.toList().reversed() to dist[current]!!

        for (next in sequence { choices(current) }) {
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

data class DijState<T>(val value: T, val dist: Int)

inline fun <T> dijkstra(
    start: T,
    initialCost: Int = 0,
    isEnd: (v: T) -> Boolean,
    crossinline cost: (u: T, v: T) -> Int,
    crossinline choices: suspend SequenceScope<T>.(T) -> Unit
): Pair<List<T>, Int> {
    val prev = mutableMapOf<T, T>()
    // needed for not 'consistent' cost function
    val dist = mutableMapOf(start to initialCost)
    // using it.dist instead of dist[it.value] makes it almost twice as fast, not sure why
    val unvisited = PriorityQueue<DijState<T>>(compareBy { it.dist })
    unvisited += DijState(start, initialCost)
    while (unvisited.isNotEmpty()) {
        val current = unvisited.remove()

        if (isEnd(current.value)) return generateSequence(current.value) { prev[it] }.toList()
            .reversed() to current.dist

        unvisited += sequence { choices(current.value) }
            .map { DijState(it, current.dist + cost(current.value, it)) }
            .filter { next -> next.value !in dist || dist[next.value]!! > next.dist } //best[next.value]?.let { it > next.dist } != true }
            .onEach {
                dist[it.value] = it.dist
                prev[it.value] = current.value
            }
    }
    error("no route")
}
data class DijStateL<T>(val value: T, val dist: Long)

inline fun <T> dijkstraL(
    start: T,
    initialCost: Long = 0,
    isEnd: (v: T) -> Boolean,
    crossinline cost: (u: T, v: T) -> Long,
    crossinline choices: suspend SequenceScope<T>.(T) -> Unit
): Pair<List<T>, Long> {
    val prev = mutableMapOf<T, T>()
    // needed for not 'consistent' cost function
    val dist = mutableMapOf(start to initialCost)
    // using it.dist instead of dist[it.value] makes it almost twice as fast, not sure why
    val unvisited = PriorityQueue<DijStateL<T>>(compareBy { it.dist })
    unvisited += DijStateL(start, initialCost)
    while (unvisited.isNotEmpty()) {
        val current = unvisited.remove()

        if (isEnd(current.value)) return generateSequence(current.value) { prev[it] }.toList()
            .reversed() to current.dist

        unvisited += sequence { choices(current.value) }
            .map { DijStateL(it, current.dist + cost(current.value, it)) }
            .filter { next -> next.value !in dist || dist[next.value]!! > next.dist } //best[next.value]?.let { it > next.dist } != true }
            .onEach {
                dist[it.value] = it.dist
                prev[it.value] = current.value
            }
    }
    error("no route")
}

data class DijPath<T>(val state: DijState<T>, val prev: List<T>)

inline fun <T> dijkstraAll(
    start: T,
    initialCost: Int = 0,
    crossinline isEnd: (v: T) -> Boolean,
    crossinline cost: (u: T, v: T) -> Int,
    crossinline choices: suspend SequenceScope<T>.(T) -> Unit
): Sequence<Pair<List<T>, Int>> = sequence {
    val unvisited = PriorityQueue<DijPath<T>>(compareBy { it.state.dist })
    val best = mutableMapOf<T, Int>()
    var bestDist = Int.MAX_VALUE
    unvisited += DijPath(DijState(start, initialCost), emptyList())
    while (unvisited.isNotEmpty()) {
        val (current, prev) = unvisited.remove()

        if (isEnd(current.value)) {
            if (current.dist <= bestDist) {
                bestDist = current.dist
                yield((prev + current.value) to current.dist)
            }
            continue
        }

        unvisited += sequence { choices(current.value) }
            .map { next -> DijState(next, current.dist + cost(current.value, next)) }
            .filter { next -> best[next.value]?.let { it < next.dist } != true }
            .onEach { best[it.value] = it.dist }
            .map { next -> DijPath(next, prev + current.value) }
    }
}