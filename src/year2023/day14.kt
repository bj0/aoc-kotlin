@file:Suppress("ObjectPropertyName")

package year2023

import util.*

fun main() {
    listOf(::solution, ::faster, ::aok).solveAll(
//        InputProvider.raw(
//            """OOOO.#.O..
//OO..#....#
//OO..O##..O
//O..#.OO...
//........#.
//..#....#.#
//..O..#.O.O
//..O.......
//#....###..
//#....#....
//""".trimEnd()
//        )
    )
}

@JvmInline
value class Table(val map: List<String>) {
    //    fun load(): Int = map.mapIndexed { i, line -> line.count() { it == 'O' } * (map.size - i) }.sum()
    fun load(): Int = map//.also { it.joinToString("\n").debug("out:\n") }
        .foldIndexed(0) { i, acc, line -> acc + line.count() { it == 'O' } * (map.size - i) }

}

private val faster = puzzle {

    val parser = parser { Table(lines) }

    fun Table.roll() = Table(
        map.map { StringBuilder(it) }.apply {
            for (x in map.first().indices) {
                var y0 = 0
                while (y0 < size) {
                    var n = 0
                    var y1 = y0
                    do {
                        val c = this[y1][x]
                        if (c == 'O') n++
                    } while (c != '#' && ++y1 < size)
                    for (y in y0 until y1) this[y][x] = if (y < y0 + n) 'O' else '.'
                    y0 = y1 + 1
                }
            }
        }.map { it.toString() }
    )

    fun Table.rotate() = Table(map.rotate())

    part1(parser) { table ->
        table.roll().load()
    }

    fun Table.cycle() = this
        .roll().rotate()
        .roll().rotate()
        .roll().rotate()
        .roll().rotate()

    part2(parser) { table ->
        val N = 1_000_000_000
        val cache = mutableMapOf(table to 0)

        for ((i, t) in generateSequence(table) { it.cycle() }.withIndex().drop(1)) {
            val j = cache.getOrPut(t) { i }
            if (i != j) {
                return@part2 cache.keys.elementAt(j + (N - i) % (i - j)).load()
            }
        }
    }
}

private val aok = puzzle {
    fun CharArray.rollRight() {
        var start = 0
        for (idx in indices) if (this[idx] == '#') {
            sort(start, idx)
            start = idx + 1
        }
        sort(start, size)
    }

    fun List<CharArray>.rotateAndRoll() = first().indices.map { x ->
        CharArray(size) { this@rotateAndRoll[lastIndex - it][x] }
            .apply(CharArray::rollRight)
    }

    fun List<CharArray>.rightLoad() = sumOf { it.indices.sumOf { x -> if (it[x] == 'O') x + 1 else 0 } }
    fun List<CharArray>.topLoad() = indices.sumOf { y -> (lastIndex - y + 1) * get(y).count('O'::equals) }

    val parse = lineParser { it.toCharArray() }
    part1(parse) { it.rotateAndRoll().rightLoad() }
    part2(parse) { initial ->
        val history = ArrayList<Pair<Int, Int>>()
        val loopStart = generateSequence(initial) {
            it.rotateAndRoll().rotateAndRoll().rotateAndRoll().rotateAndRoll()
        }.firstNotNullOf { grid ->
            val hash = grid.fold(0) { acc, chars -> acc * 31 + chars.contentHashCode() }
            val key = hash to grid.topLoad()
            history.lastIndexOf(key).takeIf { it >= 0 }
                ?: history.add(key).let { null }
        }
        val remainingSpins = 1_000_000_000 - history.size
        val loopLength = history.size - loopStart
        history[loopStart + remainingSpins % loopLength].second
    }
}

private val solution = puzzle {

    val parser = parser { Table(lines) }

    fun Table.roll() = Table(buildList {
        val moved = mutableSetOf<Point<Int>>()
        map.forEachIndexed { j, line ->
            add(buildString {
                line.forEachIndexed { i, c ->
                    fun tryMove() {
                        val toMove = (j + 1..<map.size).find { y -> map[y][i] != '.' && (i point y) !in moved }
                        if (toMove != null && map[toMove][i] == 'O') {
                            append('O')
                            moved.add(i point toMove)
                        } else {
                            append('.')
                        }
                    }
                    when (c) {
                        '#' -> append('#')
                        'O' -> if ((i point j) !in moved) append('O') else tryMove()
                        '.' -> tryMove()
                    }
                }
            })
        }
    })

    fun Table.rotate() = Table(map.rotate())

    part1(parser) { table ->
        table.roll().load()
    }

    part2(parser) { table ->
        val cache = mutableMapOf<Table, Int>()
        val cycles =
            generateSequence(table) { it.roll().rotate().roll().rotate().roll().rotate().roll().rotate() }.withIndex()
        var left: Int? = null
        cycles.dropWhile { (i, t) ->
            when (left) {
                1 -> false
                null -> {
                    if (t in cache)
                        left = (1_000_000_000 - i) % (i - cache[t]!!)
                    else cache[t] = i
                    true
                }

                else -> {
                    left = left!! - 1
                    true
                }
            }
        }.first().value.load()
    }
}

private
val `first try` = puzzle {

    val parser = parser {
        buildMap {
            lines.forEachIndexed { row, line ->
                line.forEachIndexed { col, c ->
                    put(Point(col, row), c)
                }
            }
        }
    }

    fun Map<Point<Int>, Char>.step(dir: Point<Int>.() -> Point<Int> = { up }) = buildMap {
        this@step.keys.forEach { p ->
            when {
                this@step[p] == 'O' && this@step[p.dir()] == '.' -> {
                    put(p.dir(), 'O')
                    put(p, '.')
                    Unit
                }

                else -> {
                    if (p !in this) put(p, this@step[p]!!)
                }
            }
        }
    }


    part1(parser) { map ->
        val height = map.keys.maxOf { it.y }
        val (done, _) = generateSequence(map) { it.step() }.zipWithNext().find { (a, b) -> a == b }!!
        done.filter { (_, c) -> c == 'O' }.keys.sumOf { p ->
            (height - p.y) + 1
        }
    }

    fun Map<Point<Int>, Char>.tilt(dir: Point<Int>.() -> Point<Int> = { up }) =
        generateSequence(this) { it.step(dir) }.zipWithNext().find { (a, b) -> a == b }!!.first

    fun cycle(map: Map<Point<Int>, Char>) =
        map.tilt { up }.tilt { left }.tilt { down }.tilt { right }

    part2(parser) { map ->
        val height = map.keys.maxOf { it.y }

        var cur = map
        val cache = mutableMapOf<Map<Point<Int>, Char>, Long>()

        var i = 0L
        val max = 1_000_000_000L
        while (i < max) {
            cur = cycle(cur)
            i += 1
            if (cur in cache) {
                val delta = i - cache[cur]!!
                val left = (max - i) % delta
                i = max - left
            } else {
                cache[cur] = i
            }
        }

        cur.filter { (_, c) -> c == 'O' }.keys.sumOf { p ->
            (height - p.y) + 1
        }
    }
}
