package year2024

import util.*


fun main() {
    listOf(Day4BruteForce::solution, Day4::grid).solveAll(
//        InputProvider.raw(
//            """
//            MMMSXXMASM
//            MSAMXMSMSA
//            AMXSXMAAMM
//            MSAMASMSMX
//            XMASAMXAMM
//            XXAMMXXAMA
//            SMSMSASXSS
//            SAXAMASAAA
//            MAMMMXMMMM
//            MXMXAXMASX
//        """.trimIndent()
//        )
//        InputProvider.raw(
//            """
//            .M.S......
//            ..A..MSMS.
//            .M.S.MAA..
//            ..A.ASMSM.
//            .M.S.M....
//            ..........
//            S.S.S.S.S.
//            .A.A.A.A..
//            M.M.M.M.M.
//            ..........
//        """.trimIndent()
//        )
    )
}

object Day4 : Solutions {
    val grid = puzzle {
        val parser = parser { input.toGrid() }

        part1(parser) { grid ->
            // find all 'x's and check around them
            val starts = grid.findAll { _, v -> v == 'X' }
            val xmas = "XMAS".toList()
            starts.sumOf { s ->
                GridDirection.entries.count { dir ->
                    grid.walk(s, dir).take(4).toList().containsAll(xmas)
                }
            }
        }

        part2(parser) { grid ->
            // find all 'a's and check around them
            val starts = grid.findAll { _, v -> v == 'A' }
            val ms = "MS".toList()
            starts.count { p ->
                listOf(grid[p.NorthWest], grid[p.SouthEast]).containsAll(ms) &&
                        listOf(grid[p.NorthEast], grid[p.SouthWest]).containsAll(ms)
            }
        }
    }
}

object Day4BruteForce : Solutions {
    fun List<String>.get(idx: Pair<Int, Int>) = get(idx.first)[idx.second]
    fun Pair<Int, Int>.isIn(lines: List<String>) = first in lines.indices && second in lines[0].indices

    fun count(lines: List<String>, start: Pair<Int, Int>, next: (Pair<Int, Int>) -> Pair<Int, Int>): Int {
        var cur = start
        var c = 0
        val want = "XMAS"
        var j = 0
        while (cur.isIn(lines)) {
            if (lines.get(cur) == want[j]) {
                if (j == 3) {
                    c++
                }
                j = (j + 1) % 4
                cur = next(cur)
            } else {
                if (j > 0)
                    j = 0
                else
                    cur = next(cur)
            }
        }
        return c
    }

    // first try, very ugly
    val solution = puzzle {
        part1 {
            val w = lines[0].length
            val h = lines.size
            // iterate through every possible line and cout the 'xmas' occurances
            lines.indices.sumOf { count(lines, it to 0) { (r, c) -> r to c + 1 } } +
                    lines[0].indices.sumOf { count(lines, 0 to it) { (r, c) -> r + 1 to c } } +

                    lines.indices.sumOf { count(lines, it to w - 1) { (r, c) -> r to c - 1 } } +
                    lines[0].indices.sumOf { count(lines, h - 1 to it) { (r, c) -> r - 1 to c } } +

                    lines.indices.sumOf { count(lines, it to 0) { (r, c) -> r + 1 to c + 1 } } +
                    lines[0].indices.drop(1).sumOf { count(lines, 0 to it) { (r, c) -> r + 1 to c + 1 } } +

                    lines.indices.sumOf { count(lines, it to w - 1) { (r, c) -> r + 1 to c - 1 } } +
                    lines[0].indices.toList().dropLast(1)
                        .sumOf { count(lines, 0 to it) { (r, c) -> r + 1 to c - 1 } } +

                    lines.indices.sumOf { count(lines, it to w - 1) { (r, c) -> r - 1 to c - 1 } } +
                    lines[0].indices.toList().dropLast(1)
                        .sumOf { count(lines, h - 1 to it) { (r, c) -> r - 1 to c - 1 } } +

                    lines.indices.sumOf { count(lines, it to 0) { (r, c) -> r - 1 to c + 1 } } +
                    lines[0].indices.drop(1).sumOf { count(lines, h - 1 to it) { (r, c) -> r - 1 to c + 1 } }

        }

        fun check(lines: List<String>, at: Pair<Int, Int>) =
            (lines.get(at.first + 1 to at.second + 1) == 'A') &&
                    ((lines.get(at) == 'M' && lines.get(at.first + 2 to at.second + 2) == 'S') || (lines.get(at) == 'S' && lines.get(
                        at.first + 2 to at.second + 2
                    ) == 'M'))
                    &&
                    ((lines.get(at.first + 2 to at.second) == 'M' && lines.get(at.first to at.second + 2) == 'S') || (lines.get(
                        at.first + 2 to at.second
                    ) == 'S' && lines.get(at.first to at.second + 2) == 'M'))
        part2 {
            val h = lines.size
            val w = lines[0].length
            lines.indices.filter { it < h - 2 }.sumOf { r ->
                lines[0].indices.filter { it < w - 2 }.count { c ->
                    check(lines, r to c)
                }
            }
        }
    }
}