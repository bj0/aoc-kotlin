package year2024

import util.*
import util.Direction.*
import java.util.*

fun main() {
    Day21.solveAll(
        InputProvider.raw(
            """
        029A
        980A
        179A
        456A
        379A
    """.trimIndent()
        )
    )

    Day21.solveAll()
}

object Day21 : Solutions {
    data class Pad(private val pad: Map<LongPoint, Char>) {
        private val rpad = pad.entries.associate { (k, v) -> v to k }

        operator fun get(char: Char) = rpad.getValue(char)
        operator fun get(point: LongPoint) = pad.getValue(point)
        operator fun contains(it: LongPoint) = it in pad
    }

    val keypad = Pad(
        """
                789
                456
                123
                .0A
            """.trimIndent().lines().toMapGrid().filter { (_, c) -> c != '.' })
    val dpad = Pad(
        """
                .^A
                <v>
            """.trimIndent().lines().toMapGrid().filter { (_, c) -> c != '.' })


    infix fun LongPoint.dirsTo(other: LongPoint) = buildSet {
        val (dx, dy) = other - this@dirsTo
        when {
            dx < 0L -> add(Left)
            dx > 0L -> add(Right)
        }
        when {
            dy < 0L -> add(Up)
            dy > 0L -> add(Down)
        }
    }

    val Direction.button
        get() = when (this) {
            Up -> '^'
            Down -> 'v'
            Right -> '>'
            Left -> '<'
        }

    data class State(val prev: Char, val next: Char, val depth: Int)
    data class State2(val pos: LongPoint, val prev: Char, val cost: Long)

    val cache = mutableMapOf<State, Long>()
    // num of presses to press "prev" then "next"
    fun State.distance(pad: Pad): Long = (cache.getOrPut(this) {
        if (depth == -1) return@getOrPut 1L
        if (prev == next) return@getOrPut 1L
        val start = pad[prev]
        val end = pad[next]
        val dirs = start dirsTo end
        val q = PriorityQueue<State2>(compareBy { it.cost })
        q += State2(start, 'A', 0)
        val seen = mutableSetOf<Pair<LongPoint, Char>>()
        while (q.isNotEmpty()) {
            val (cur, pmove, cost) = q.remove()
            if (cur to pmove in seen) continue
            seen += cur to pmove
            if (cur == end) {
                if (pmove == 'A') return@getOrPut cost
                q += State2(cur, 'A', cost + State(pmove, 'A', depth - 1).distance(dpad))
                continue
            }
            q += dirs.map { cur + it to it }.filter { it.first in pad }
                .map {
                    State2(
                        it.first,
                        it.second.button,
                        cost + State(pmove, it.second.button, depth - 1).distance(dpad)
                    )
                }
        }
        error("no path")
    })

    val cleanup = puzzle {
        fun solve(code: String, pads: Int = 2): Long {
            return "A$code".zipWithNext { from, to ->
                State(from, to, pads).distance(keypad)
            }.sum() * code.dropLast(1).toLong()
        }


        part1 {
            lines.sumOf { solve(it) }
        }

        part2 {
            lines.sumOf { solve(it, 25) }
        }
    }

    // 4th tries the charm!
    val try4 = puzzle {
        val keypad = """
                789
                456
                123
                .0A
            """.trimIndent().lines().toMapGrid().filter { (_, c) -> c != '.' }
        val dpad = """
                .^A
                <v>
            """.trimIndent().lines().toMapGrid().filter { (_, c) -> c != '.' }

        data class Key(val move: Direction?, val last: Direction?, val pads: Int)
        data class Other(val pos: LongPoint, val last: Direction?, val cost: Long, val out: String)

        val cache = mutableMapOf<Key, Long>()
        fun cost(move: Direction?, last: Direction?, pads: Int): Long =
            cache.getOrPut(Key(move, last, pads)) {
                if (pads == 0) return@getOrPut 1L
                val code = move?.button?.toString() ?: "A"
                val start = dpad.entries.first { it.value == (last?.button ?: 'A') }.key
                val q = PriorityQueue<Other>(compareBy { it.cost })
                q += Other(start, null, 0L, "")
                val seen = mutableSetOf<Pair<LongPoint, Direction?>>()
                while (q.isNotEmpty()) {
                    val a = q.remove()
                    val (pos, last, cost, out) = a
                    if (out == code) return@getOrPut cost
                    if (out.isNotEmpty()) continue
                    val x = pos to last
                    if (x in seen) continue
                    seen += x

                    (Direction.entries + null).forEach { m ->
                        val pos2 = m?.let { pos + it } ?: pos
                        val c = cost + cost(m, last, pads - 1)
                        if (pos2 in dpad) {
                            val out1 = if (m == null) out + dpad[pos2] else out
                            q += Other(pos2, m, c, out1)
                        }
                    }
                }
                error("no code $code, $move, $last, $pads, $start")
            }


        fun solve(code: String, pads: Int = 2): Long {
            val A = keypad.entries.first { it.value == 'A' }.key
            val q = PriorityQueue<Other>(compareBy { it.cost })
            q += Other(A, null, 0, "")
            val seen = mutableSetOf<Triple<LongPoint, Direction?, String>>()
            var best = ""
            while (q.isNotEmpty()) {
                val a = q.remove()
                val (pos, last, cost, out) = a
                if (out == code) return cost
                if (!code.startsWith(out)) continue
                if (out.length < best.length) continue
                best = out
                val x = Triple(pos, last, out)
                if (x in seen) continue
                seen += x

                (Direction.entries + null).forEach { m ->
                    val pos2 = m?.let { pos + it } ?: pos
                    if (pos2 in keypad) {
                        val out1 = if (m == null) out + keypad[pos2] else out
                        q += Other(pos2, m, cost + cost(m, last, pads), out1)
                    }
                }

            }
            error("no code")
        }

        part1 {
            lines.sumOf { code ->
                solve(code) * code.dropLast(1).toInt()
            }
        }

        part2 {
            lines.sumOf { code ->
                solve(code, 25) * code.dropLast(1).toInt()
            }
        }
    }
}
