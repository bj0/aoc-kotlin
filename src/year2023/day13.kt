package year2023

import util.PuzDSL
import util.debug
import util.solveAll

fun main() {
    listOf(Day13).solveAll(
//            InputProvider.Example
    )
}

object Day13 : PuzDSL({

    part1 {
        val patterns = input.split("\n\n")
        patterns.sumOf { pat ->
            100 * pat.findHorizontal() + pat.findVertical()
        }
    }

    part2 {
        val patterns = input.split("\n\n")
        patterns.sumOf { pat ->
            100 * pat.findAlmostHorizontal() + pat.findAlmostVertical()
        }
    }


})

private fun String.findAlmostVertical(): Int {
    val lines = split('\n')
    val vlines = lines[0].indices.map { lines.map { line -> line[it] }.joinToString("") }
    val centers = vlines.zipWithNext().mapIndexedNotNull { i, (a, b) -> if ((a diff b <= 1)) i else null }
    return centers.find { vlines.diffs(it) == 1 }?.let { it + 1 } ?: 0
}

private fun String.findAlmostHorizontal(): Int {
    val lines = split('\n')
    val centers = lines.zipWithNext().mapIndexedNotNull { i, (a, b) -> if (a diff b <= 1) i else null }.debug("h:")
    return centers.find { lines.diffs(it) == 1 }.debug("hf")?.let { it + 1 } ?: 0
//    return centers.filter { lines.checkAlmost(it) }.sumOf { it+1 }

}

private fun String.findVertical(): Int {
    val lines = split('\n')
    val vlines = lines[0].indices.map { lines.map { line -> line[it] }.joinToString("") }
    val centers = vlines.zipWithNext().mapIndexedNotNull { i, (a, b) -> if (a == b) i else null }
    return centers.filter { vlines.check(it) }.sumOf { it + 1 }
}

private fun String.findHorizontal(): Int {
    val lines = split('\n')
    val centers = lines.zipWithNext().mapIndexedNotNull { i, (a, b) -> if (a == b) i else null }
    return centers.filter { lines.check(it) }.sumOf { it + 1 }
}

private fun List<String>.check(idx: Int) =
        ((idx downTo 0) zip (idx + 1..<size)).all { (a, b) -> this[a] == this[b] }


private fun List<String>.diffs(idx: Int) =
        ((idx downTo 0) zip (idx + 1..<size)).sumOf { (ia, ib) ->
            this[ia] diff this[ib]
        }

private infix fun String.diff(other: String) = this.withIndex().count { (i, c) -> c != other[i] }