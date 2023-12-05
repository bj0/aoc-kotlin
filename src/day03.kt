const val NUM = "0123456789"

fun main() {
    fun part1(input: List<String>): Int = with(input.grid()) {
        data.filterValues { !NUM.contains(it) }.flatMap { (p, _) ->
            p.neighbors().mapNotNull { findNumber(it) }
        }.toSet()
            .sumOf { (_, n) -> n.toInt() }
    }

    fun part2(input: List<String>): Int = with(input.grid()) {
        find('*').map { p ->
            p.neighbors().mapNotNull { findNumber(it) }.toSet()
        }.filter { it.size == 2 }
            .map { it.map { (_, n) -> n.toInt() }.reduce(Int::times) }
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day03_test")
    check(part1(testInput) == 4361)

    val input = readInput("day03")
    part1(input).println()
    part2(input).println()
}

data class Point(val x: Int, val y: Int)

operator fun Point.plus(other: Point) = Point(x + other.x, y + other.y)

data class Grid<T>(val data: Map<Point, T>)

fun List<String>.grid(vararg ignore: String): Grid<Char> = Grid(this.flatMapIndexed { j, row ->
    val ignore = if (ignore.isEmpty()) listOf(".") else ignore.toList()
    row.mapIndexedNotNull { i, char ->
        if (!ignore.contains(char.toString())) Point(i, j) to char else null
    }
}.toMap())

fun <T> Grid<T>.find(item: T): Sequence<Point> = sequence {
    yieldAll(data.keys.filter { data[it] == item })
}

context(Grid<Char>)
fun Point.neighbors() = sequence {
    (-1..1).forEach { i ->
        (-1..1).forEach { j ->
            if (!(i == 0 && j == 0)) {
                val p = Point(x + i, y + j)
                if (p in data)
                    yield(p)
            }
        }
    }
}

context(Grid<Char>)
fun findNumber(point: Point): Pair<Point, String>? {
    if (!NUM.contains(data[point] ?: '.'))
        return null
    var p = point + Point(-1, 0)
    while ((p in data) && NUM.contains(data[p]!!))
        p += Point(-1, 0)
    p += Point(1, 0)
    val start = p
    var num = ""
    while ((p in data) && NUM.contains(data[p]!!)) {
        num += data[p]
        p += Point(1, 0)
    }
    return start to num
}