package util

context(pi: PuzzleInput)
val lines get() = pi.lines

context(pi: PuzzleInput)
val input get() = pi.input

context(ms: MathScope<T>)
val <T> T.absoluteValue get() = with(ms) { absoluteValue }

context(ps: PointScope<T, P>)
infix fun <T, P : GPoint<T>> T.point(y: T) = with(ps) { this@point point y }

context(ss: SolutionsScope<P1, P2>)
@SolutionDsl
fun <P1, P2> part1(solution: Solution<P1>) = ss.part1(solution)

context(ss: SolutionsScope<P1, P2>)
@SolutionDsl
fun <P1, P2> part2(solution: Solution<P2>) = ss.part2(solution)

context(ss: SolutionsScope<P1, P2>)
@SolutionDsl
fun <P1, P2, R> part1(parser: Parser<R>, solution: suspend (R) -> P1) = ss.part1 { solution(parser.parse()) }

context(ss: SolutionsScope<P1, P2>)
@SolutionDsl
fun <P1, P2, R> part2(parser: Parser<R>, solution: suspend (R) -> P2) = ss.part2 { solution(parser.parse()) }

context(ss: SolutionsScope<P1, P2>)
@SolutionDsl
fun <P1, P2, R> parser(block: Parser<R>) = ss.parser(block)