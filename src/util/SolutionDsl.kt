package util

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.time.Duration
import kotlin.time.TimedValue
import kotlin.time.measureTimedValue

@DslMarker
annotation class SolutionDsl

fun interface Parser<T> {
    context(PuzzleInput)
    fun parse(): T

    fun <R> map(f: (T) -> R) = Parser { f(parse()) }
    fun <R> andThen(f: (T) -> R) = map(f)
    context(PuzzleInput)
    operator fun invoke() = parse()

    operator fun invoke(input: String) = with(PuzzleInput.of(input)) { parse() }
}

fun <R> lineParser(mapper: (line: String) -> R) = Parser { lines.map(mapper) }
fun <T, R> Parser<List<T>>.map(mapper: (T) -> R) = map { it.map(mapper) }

@SolutionDsl
interface SolutionsScope<P1, P2> {
    fun <R> parser(block: Parser<R>) = block
    fun <R> lineParser(mapper: (line: String) -> R) = util.lineParser(mapper)

    @SolutionDsl
    fun part1(solution: Solution<P1>)

    @SolutionDsl
    fun part2(solution: Solution<P2>)

    @SolutionDsl
    fun <R> part1(parser: Parser<R>, solution: suspend (R) -> P1) = part1 { solution(parser.parse()) }

    @SolutionDsl
    fun <R> part2(parser: Parser<R>, solution: suspend (R) -> P2) = part2 { solution(parser.parse()) }
}

@SolutionDsl
fun interface PuzzleDefinition<P1, P2> {
    context(SolutionsScope<P1, P2>) fun build()
}

@SolutionDsl
fun interface Solution<T> {
    @SolutionDsl
    suspend fun PuzzleInput.solve(): T
}

private operator fun <P1, P2> PuzzleDefinition<P1, P2>.provideDelegate(thisRef: Any?, property: KProperty<*>) = lazy {
    var part1 = Solution<P1> { TODO() }
    var part2 = Solution<P2> { TODO() }
    with(object : SolutionsScope<P1, P2> {
        override fun part1(solution: Solution<P1>) {
            part1 = solution
        }

        override fun part2(solution: Solution<P2>) {
            part2 = solution
        }
    }) { this@provideDelegate.build() }
    part1 to part2
}

fun puzzle(body: PuzzleDefinition<*, *>): Puzzle<out Any?, out Any?> {
    val solution by body
    return Puzzle(solution.first, solution.second)
}


class Puzzle<P1, P2>(private val solution1: Solution<P1>, private val solution2: Solution<P2>) {
    context(PuzzleInput)
    suspend fun part1() = with(solution1) { solve() }

    context(PuzzleInput)
    suspend fun part2() = with(solution2) { solve() }
}

abstract class PuzDSL(body: PuzzleDefinition<*, *>) {
    val solutions by body
    context(PuzzleInput)
    suspend fun part1() = with(solutions.first) { solve() }

    context(PuzzleInput)
    suspend fun part2() = with(solutions.second) { solve() }
}

fun KProperty0<Puzzle<*, *>>.solve(
    input: InputProvider = InputProvider.Default
) = listOf(this).solveAll(input)

fun List<KProperty0<Puzzle<*, *>>>.solveAll(input: InputProvider = InputProvider.Default) = with(input) {
    map { it.resolvePuzzle() }.solveAll()
}

fun KProperty0<Puzzle<*, *>>.resolvePuzzle(): Puz<Any?, Any?> {
    val parts = javaClass.name.split('.')
    val year = parts.first().substringAfter("year").toInt()
    val day = parts.last().substringBefore('$').getInts().first()
    val variant = name;
    return get().resolvePuzzle(year, day, variant)
}

fun PuzDSL.solveAll(
    input: InputProvider = InputProvider.Default
) = listOf(this@solveAll).solveAll(input)

fun Iterable<PuzDSL>.solveAll(
    input: InputProvider = InputProvider.Default
) = with(input) { map { it.resolvePuzzle() }.solveAll() }


context(InputProvider)
fun Iterable<Puz<*, *>>.solveAll(runIterations: Int = 1) =
    sortedWith(compareBy<Puz<*, *>> { it.year }.thenBy { it.day })
        .groupBy { it.year to it.day }.forEach { (year, day), puzzles ->
            with(forPuzzle(year, day)) {
                fun runPart(part: Puz<*, *>.() -> Any?) {
                    val results = puzzles.map { puz ->
                        puz.variant to runCatching {
                            measureTimedValue {
                                repeat(runIterations - 1) { puz.part() }
                                puz.part()
                            }.let { it.copy(duration = it.duration / runIterations) }
                        }.getOrElse {
                            TimedValue(
                                if (it is NotImplementedError) it else it.stackTraceToString(),
                                Duration.INFINITE
                            )
                        }
                    }.sortedBy { (_, it) -> it.duration }
                    val fastest = results.minOf { it.second.duration }
                    results.forEach { (variant, it) ->
                        val result = it.value.toResultString()
                        println("\t $variant took ${it.duration} (${"%.2f".format(it.duration / fastest)}x): $result")
                    }
                }

                println("year $year day $day part 1")
                runPart { part1() }
                println("year $year day $day part 2")
                runPart { part2() }
            }
            println("")
        }

private fun Any?.repr() = when (this) {
    null -> "<null>"
    is Array<*> -> contentDeepToString()
    is BooleanArray -> contentToString()
    is ByteArray -> contentToString() // TODO: hex/base64?
    is ShortArray -> contentToString()
    is IntArray -> contentToString()
    is LongArray -> contentToString()
    is FloatArray -> contentToString()
    is DoubleArray -> contentToString()
    else -> toString()
}

private fun Any?.toResultString() = repr().let {
    if ('\n' in it) "\n" + it.lines().joinToString("\n") { line -> "\t\t$line" }
    else it
}

// default inputs
fun Iterable<Puz<*, *>>.solveAll(runIterations: Int = 1) = with(InputProvider) { solveAll(runIterations) }
