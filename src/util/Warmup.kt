package util

import kotlinx.coroutines.runBlocking
import kotlin.time.Duration
import kotlin.time.measureTime

sealed interface Warmup {
    context(PuzzleInput)
    fun run(year: Int, day: Int, puzzles: List<Puz<*, *>>)

    private object None : Warmup {
        context(PuzzleInput) override fun run(year: Int, day: Int, puzzles: List<Puz<*, *>>) = Unit
    }

    private class Iterations(val warmupIterations: Int) : Warmup {
        context(PuzzleInput) override fun run(year: Int, day: Int, puzzles: List<Puz<*, *>>) {
            if (warmupIterations > 0) {
                println("Warming up ${puzzles.size} puzzles $warmupIterations times for year $year day $day...")
                measureTime {
                    repeat(warmupIterations) {
                        puzzles.forEach {
                            runCatching { it.part1() }
                            runCatching { it.part2() }
                        }
                    }
                }.also { println("year $year day $day warmup ($warmupIterations iterations) took $it") }
            }
        }
    }

    companion object {
        fun iterations(n: Int): Warmup = Iterations(n)
        val none: Warmup = None
    }
}

context(InputProvider)
fun Iterable<Puz<*, *>>.warmup(warmup: Warmup) = apply {
    groupBy { it.year to it.day }.forEach { (year, day), puzzles ->
        with(forPuzzle(year, day)) {
            warmup.run(year, day, puzzles)
        }
    }
}

context(InputProvider)
fun Iterable<Puz<*, *>>.warmup(iterations: Int) = warmup(Warmup.iterations(iterations))

fun Iterable<Puz<*, *>>.warmup(iterations: Int = 1) = with(InputProvider) { warmup(iterations) }
