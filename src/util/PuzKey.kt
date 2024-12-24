package util

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@Serializable
sealed interface PuzKey {
    val year: Int
    val day: Int
    val variant: String

    companion object {
        fun of(year: Int, day: Int, variant: String): PuzKey = Impl(year, day, variant)
    }

    @Serializable
    data class Impl(override val year: Int, override val day: Int, override val variant: String) : PuzKey
}

interface Puz<out P1, out P2> : PuzKey {
    context(PuzzleInput) fun part1(): P1 = TODO()
    context(PuzzleInput) fun part2(): P2 = TODO()
}


fun PuzDSL.resolvePuzzle(): Puz<Any?, Any?> {
    val parts = this::class.qualifiedName!!.split('.')
    val year = parts.first().substringAfter("year").toInt()
    val day = parts.last().getInts().last()
    val variant = parts.last().substringAfter("Day").dropWhile { it.isDigit() }

    return object : Puz<Any?, Any?> {
        override val year: Int = year
        override val day: Int = day
        override val variant: String = variant.takeIf { it.isNotEmpty() } ?: "Default"

        context(PuzzleInput) override fun part1(): Any? =
            runBlocking { this@resolvePuzzle.part1() }


        context(PuzzleInput) override fun part2(): Any? =
            runBlocking { this@resolvePuzzle.part2() }

        override fun toString() = "Puzzle[year=$year,day=$day,variant=$variant]"
    }
}

fun <P1, P2> Puzzle<P1, P2>.resolvePuzzle(year: Int, day: Int, variant: String): Puz<P1, P2> {
    return object : Puz<P1, P2> {
        override val year: Int = year
        override val day: Int = day
        override val variant: String = variant.takeIf { it.isNotEmpty() } ?: "Default"

        context(PuzzleInput) override fun part1(): P1 =
            runBlocking { this@resolvePuzzle.part1() }


        context(PuzzleInput) override fun part2(): P2 =
            runBlocking { this@resolvePuzzle.part2() }

        override fun toString() = "Puzzle[year=$year,day=$day,variant=$variant]"
    }
}

