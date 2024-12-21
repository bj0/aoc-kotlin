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

    data class State(val prevKey: Char, val nextKey: Char, val depth: Int)

    // prevMove is the prev key pressed on the lower pad
    data class InnerState(val curKey: Char, val prevMove: Char, val cost: Long)

    // num of presses to press "prev" then "next"
    val cache = mutableMapOf<State, Long>()
    fun State.pressCount(pad: Pad): Long = (cache.getOrPut(this) {
        // human press || no move, just press
        if (depth == -1 || prevKey == nextKey) return@getOrPut 1L
        val possibleDirections = pad[prevKey] dirsTo pad[nextKey]
        val q = PriorityQueue<InnerState>(compareBy { it.cost })
        q += InnerState(prevKey, 'A', 0)
        while (q.isNotEmpty()) {
            val (curKey, prevMove, cost) = q.remove()
            when {
                curKey == nextKey && prevMove == 'A' -> return@getOrPut cost
                // dont' forget to press the key when you get there
                curKey == nextKey -> q += InnerState(
                    curKey,
                    'A',
                    cost + State(prevMove, 'A', depth - 1).pressCount(dpad)
                )

                else -> q += possibleDirections.map { pad[curKey] + it to it }.filter { it.first in pad }
                    .map {
                        InnerState(
                            pad[it.first],
                            it.second.button,
                            cost + State(prevMove, it.second.button, depth - 1).pressCount(dpad)
                        )
                    }
            }
        }
        error("no path")
    })

    val cleanup = puzzle {
        fun solve(code: String, pads: Int = 2): Long {
            return "A$code".zipWithNext { from, to ->
                State(from, to, pads).pressCount(keypad)
            }.sum() * code.dropLast(1).toLong()
        }


        part1 {
            lines.sumOf { solve(it) }
        }

        part2 {
            lines.sumOf { solve(it, 25) }
        }
    }

    val cache2 = mutableMapOf<State, Long>()

    // num of presses to press "prev" then "next"
    fun State.pressCountDij(pad: Pad): Long = (cache2.getOrPut(this) {
        // human press
        // no move, just press
        if (depth == -1 || prevKey == nextKey) return@getOrPut 1L
        val possibleDirections = pad[prevKey] dirsTo pad[nextKey]
        return dijkstraL(
            start = prevKey to 'A',
            initialCost = 0L,
            isEnd = { (cur, prevMove) -> cur == nextKey && prevMove == 'A' },
            cost = { u, v -> State(u.second, v.second, depth - 1).pressCount(dpad) }
        ) { (pos, _) ->
            // dont' forget to press it when you get there
            if (pos == nextKey) yield(pos to 'A') else
                yieldAll(possibleDirections.map { pad[pos] + it to it }.filter { it.first in pad }
                    .map { pad[it.first] to it.second.button })
        }.second
    })

    val dij = puzzle {
        fun solve(code: String, pads: Int = 2): Long {
            return "A$code".zipWithNext { from, to ->
                State(from, to, pads).pressCountDij(keypad)
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


    // this solution iterates all paths (no dij) and just takes shortest

    fun String.toKeypad() = lines().toMapGrid().entries.associateBy({ it.value }, { it.key })

    val numberspad = """
        789
        456
        123
        -0A
    """.trimIndent().toKeypad()

    val directionpad = """
        -^A
        <v>
    """.trimIndent().toKeypad()

    fun List<String>.solve(size: Int) = sumOf { it.sizeOfCommands(size) * it.getIntList()[0] }

    fun String.sizeOfCommands(depth: Int, first: Boolean = true): Long = "A$this"
        .zipWithNext { a, b -> State(a, b, depth).solve(first) }
        .sum()

    //    data class State(val from: Char, val to: Char, val depth: Int)
    val memoization = mutableMapOf<State, Long>()
    fun State.solve(first: Boolean): Long = memoization.getOrPut(this) {
        val keypad = if (first) numberspad else directionpad
        val routes = routes(keypad)
        when (depth) {
            0 -> routes.first().length.toLong()
            else -> routes.minOf { it.sizeOfCommands(depth - 1, false) }
        }
    }

    fun State.routes(keypad: Map<Char, LongPoint>): List<String> {
        val start = keypad.getValue(prevKey)
        val goal = keypad.getValue(nextKey)
        val hole = keypad.getValue('-')
        return generateSequence(listOf(start to emptyList<Direction>())) { frontier ->
            frontier.flatMap { (end, path) ->
                when (end) {
                    hole -> emptyList()
                    else -> listOfNotNull(
                        if (end.x > goal.x) end + Left to path + Left else null,
                        if (end.x < goal.x) end + Right to path + Right else null,
                        if (end.y > goal.y) end + Up to path + Up else null,
                        if (end.y < goal.y) end + Down to path + Down else null
                    )
                }
            }
        }.takeWhile { it.isNotEmpty() }.last().map { (_, line) -> line.toCharacters() }
    }

    fun List<Direction>.toCharacters() = map {
        when (it) {
            Up -> '^'
            Down -> 'v'
            Right -> '>'
            Left -> '<'
        }
    }.joinToString("", postfix = "A")

    val other = puzzle {
        part1 {
            lines.solve(2)
        }

        part2 {
            lines.solve(25)
        }
    }

}

