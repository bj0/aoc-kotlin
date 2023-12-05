fun main() {
    fun part1(input: Map<Int, List<List<Map<String, Int>>>>): Int = input.filterValues { groups ->
        groups.all { cols ->
            cols.maxOf { it["blue"] ?: 0 } <= 14 &&
                    cols.maxOf { it["red"] ?: 0 } <= 12 &&
                    cols.maxOf { it["green"] ?: 0 } <= 13
        }
    }.keys.sum()

    fun part2(input: Map<Int, List<List<Map<String, Int>>>>): Int = input.map { (_, groups) ->
        listOf("red", "green", "blue").map { c ->
            groups.maxOf { g -> g.maxOf { col -> col[c] ?: 0 } }
        }.reduce(Int::times)
    }.sum()

    val pat = """(\d+) (\w+),?""".toRegex()
    fun List<String>.parsed() = this.mapIndexed { i, line ->
        i + 1 to line.strip().split(";").map { group ->
            pat.findAll(group).map {
                val (n, col) = it.destructured
                col to n.toInt()
            }.toMap()
        }
    }.groupBy({ it.first }, { it.second })


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day02_test").parsed()
    check(part1(testInput) == 8)

    val input = readInput("day02").parsed()
    part1(input).println()
    part2(input).println()
}
