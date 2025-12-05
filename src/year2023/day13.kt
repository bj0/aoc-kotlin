@file:Suppress("ObjectPropertyName")

package year2023

import util.solution
import util.solveAll
import util.transpose

fun main() {
    listOf(::sol, ::`first try`).solveAll()
}

private val sol = solution {
    // this assumes only a single mirror line
    fun List<String>.findMirror(eq: List<String>.(List<String>) -> Boolean): Int =
        (1..lastIndex).find { n -> take(n).asReversed().eq(drop(n)) } ?: 0

    infix fun String.diff(other: String) = withIndex().count { (i, c) -> c != other[i] }

    fun List<String>.same(lines: List<String>) = asSequence().zip(lines.asSequence()).all { (a, b) -> a == b }

    fun List<String>.almostSame(lines: List<String>) = zip(lines).sumOf { (a, b) -> a diff b } == 1

    fun List<List<String>>.summarize(eq: List<String>.(List<String>) -> Boolean) = sumOf { pat ->
        100 * pat.findMirror(eq) + pat.transpose().findMirror(eq)
    }

    part1 {
        input.split("\n\n").map(String::lines).summarize(List<String>::same)
    }

    part2 {
        input.split("\n\n").map(String::lines).summarize(List<String>::almostSame)
    }
}

private val `first try` = solution {
    infix fun String.diff(other: String) = this.withIndex().count { (i, c) -> c != other[i] }

    fun List<String>.check(idx: Int) =
        ((idx downTo 0) zip (idx + 1..<size)).all { (a, b) -> this[a] == this[b] }


    fun List<String>.diffs(idx: Int) =
        ((idx downTo 0) zip (idx + 1..<size)).sumOf { (ia, ib) ->
            this[ia] diff this[ib]
        }

    fun String.findAlmostVertical(): Int {
        val lines = lines()
        val vlines = lines[0].indices.map { lines.map { line -> line[it] }.joinToString("") }
        val centers = vlines.zipWithNext().mapIndexedNotNull { i, (a, b) -> if ((a diff b <= 1)) i else null }
        return centers.find { vlines.diffs(it) == 1 }?.let { it + 1 } ?: 0
    }

    fun String.findAlmostHorizontal(): Int {
        val lines = lines()
        val centers = lines.zipWithNext().mapIndexedNotNull { i, (a, b) -> if (a diff b <= 1) i else null }
        return centers.find { lines.diffs(it) == 1 }?.let { it + 1 } ?: 0
//    return centers.filter { lines.checkAlmost(it) }.sumOf { it+1 }

    }

    fun String.findVertical(): Int {
        val lines = lines()
        val vlines = lines[0].indices.map { lines.map { line -> line[it] }.joinToString("") }
        val centers = vlines.zipWithNext().mapIndexedNotNull { i, (a, b) -> if (a == b) i else null }
        return centers.filter { vlines.check(it) }.sumOf { it + 1 }
    }

    fun String.findHorizontal(): Int {
        val lines = split('\n')
        val centers = lines.zipWithNext().mapIndexedNotNull { i, (a, b) -> if (a == b) i else null }
        return centers.filter { lines.check(it) }.sumOf { it + 1 }
    }
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
}