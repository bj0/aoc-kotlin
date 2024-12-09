package year2024

import util.InputProvider
import util.Solutions
import util.puzzle
import util.solveAll

fun main() {
    Day09.solveAll(
        InputProvider.raw(
            """2333133121414131402""".trimIndent()
        )
    )

    Day09.solveAll()
}

object Day09 : Solutions {
    val solution = puzzle {
        part1 {
            // build pattern
            val list = sequence {
                input.toList().map { it.digitToInt() }.chunked(2).forEachIndexed { i, szs ->
                    repeat(szs.first()) { yield(i) }
                    if (szs.size > 1)
                        repeat(szs.last()) { yield(-1) }
                }
            }.toList()

            // build new pattern
            sequence {
                val q = list.dropLastWhile { it == -1 }.toMutableList()
                while (q.isNotEmpty()) {
                    when (val n = q.removeFirst()) {
                        -1 -> {
                            yield(q.removeLast())
                            while (q.lastOrNull() == -1) q.removeLast()
                        }

                        else -> yield(n)
                    }
                }
            }.foldIndexed(0L) { index, acc, i -> acc + index * i }
        }

        part2 {
            data class Chunk(val id: Long, val size: Int, val pos: Long) {
                val isFree = id == -1L
            }

            val (free, mem) = input.toList().map { it.digitToInt() }.runningFoldIndexed(Chunk(-1, 0, 0)) { i, acc, n ->
                Chunk(if (i % 2 == 0) i / 2L else -1L, n, acc.pos + acc.size)
            }.drop(1).partition { it.isFree }

            val mfree = free.toMutableList()
            val sorted = mem.asReversed().map { frm ->
                val toi = mfree.indexOfFirst { it.size >= frm.size }
                if (toi >= 0) {
                    val to = mfree[toi]
                    if (to.pos < frm.pos) {
                        mfree[toi] = to.copy(size = to.size - frm.size, pos = to.pos + frm.size)
                        frm.copy(pos = to.pos)
                    } else frm
                } else frm
            }.sortedBy { it.pos }

            sorted.fold(0L) { acc, n -> acc + (n.pos * n.size + n.size * (n.size - 1) / 2) * n.id }
        }
    }
}