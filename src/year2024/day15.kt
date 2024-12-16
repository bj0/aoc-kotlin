package year2024

import util.*


fun main() {
    Day15.solveAll(

        InputProvider.raw(
//            """
//                #######
//                #...#.#
//                #.....#
//                #..OO@#
//                #..O..#
//                #.....#
//                #######
//
//                <vv<<^^<<^^
//            """.trimIndent()
//            """
//            ########
//            #..O.O.#
//            ##@.O..#
//            #...O..#
//            #.#.O..#
//            #...O..#
//            #......#
//            ########
//
//            <^^>>>vv<v>>v<<
//        """.trimIndent()
            """
        ##########
        #..O..O.O#
        #......O.#
        #.OO..O.O#
        #..O@..O.#
        #O#..O...#
        #O..O..O.#
        #.OO.O.OO#
        #....O...#
        ##########

        <vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
        vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
        ><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
        <<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
        ^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
        ^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
        >^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
        <><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
        ^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
        v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
    """.trimIndent()
        )
    )
    Day15.solveAll()
}

object Day15 : Solutions {
    enum class Direction(var char: Char) {
        Left('<'),
        Up('^'),
        Right('>'),
        Down('v'),
        ;

        operator fun unaryMinus() = when (this) {
            Left -> Right
            Right -> Left
            Up -> Down
            Down -> Up
        }

        companion object {
            private val byChar = entries.associateBy { it.char }
            fun parse(input: Char): Direction {
                return byChar[input] ?: error("Invalid char=$input")
            }
        }
    }

    private fun LongPoint.step(dir: Direction) = when (dir) {
        Direction.Left -> left
        Direction.Up -> up
        Direction.Right -> right
        Direction.Down -> down
    }

    operator fun LongPoint.plus(dir: Direction) = step(dir)

    private fun move(
        walls: Set<LongPoint>,
        boxes: Set<LongPoint>,
        from: LongPoint,
        direction: Direction,
        robot: Boolean = true
    ): Pair<Boolean, Set<LongPoint>> {
        return when (val n = from.step(direction)) {
            in walls -> false to emptySet()
            !in boxes -> {
                true to if (robot) boxes else boxes + n
            }

            else -> {
                val moved = if (robot) boxes - n else boxes
                move(walls, moved, n, direction, false)
            }
        }
    }

    sealed interface Item {
        val location: LongPoint

        fun tryPush(
            walls: Set<Wall>,
            boxes: Set<Box>,
            dir: Direction
        ): Pair<Boolean, List<Item>>
    }

    operator fun Set<Item>.contains(point: LongPoint) = any { it.location == point }
    operator fun Set<Box>.get(point: LongPoint) = firstOrNull { it.location == point }

    @JvmInline
    value class Wall(override val location: LongPoint) : Item {
        override fun tryPush(
            walls: Set<Wall>,
            boxes: Set<Box>,
            dir: Direction
        ) = false to emptyList<Wall>()

        override fun toString() = "$location"
    }

    @JvmInline
    value class Robot(override val location: LongPoint) : Item {
        override fun tryPush(
            walls: Set<Wall>,
            boxes: Set<Box>,
            dir: Direction
        ): Pair<Boolean, List<Box>> {
            val n = location + dir
            // check walls
            if (n in walls) return false to emptyList()
            // check boxes
            return true to listOf(n, n + Direction.Left).mapNotNull { boxes[it] }
        }

        override fun toString() = "$location"
    }


    @JvmInline
    value class Box(override val location: LongPoint) : Item {
        override fun tryPush(
            walls: Set<Wall>,
            boxes: Set<Box>,
            dir: Direction
        ): Pair<Boolean, List<Box>> {
            val left = location + dir
            val right = left + Direction.Right
            // check walls
            if (left in walls || right in walls) return false to emptyList()
            // check boxes
            return true to listOf(left, left + Direction.Left, right).mapNotNull { boxes[it] }
        }

        override fun toString() = "$location"
    }

    val solution = puzzle {
        part1 {
            val (map, dirsList) = input.split("\n\n")
            val grid = map.lines().toMapGrid()
            val dirs = dirsList.lines().joinToString("").toCharArray().map { Direction.parse(it) }

            var boxes = grid.findAll { _, c -> c == 'O' }
            val walls = grid.findAll { _, c -> c == '#' }
            var robot = grid.find { _, c -> c == '@' }!!

            dirs.forEach { dir ->
                val (did, moved) = move(walls, boxes, robot, dir)
                if (did) {
                    robot = robot.step(dir)
                    boxes = moved
                }
            }

            boxes.sumOf { it.y * 100 + it.x }
        }

        fun tryPush(walls: Set<Wall>, boxes: Set<Box>, robot: Robot, dir: Direction): Pair<Boolean, Set<Box>> {
            val (worked, start) = robot.tryPush(walls, boxes, dir)
            if (!worked) return false to emptySet()
            val q = ArrayDeque(start)
//            val q = start.toMutableList()
            val moved = mutableSetOf<Box>()
            while (q.isNotEmpty()) {
                val n = q.removeFirst()
                if (n in moved) continue
                moved.add(n)
                val (can, items) = n.tryPush(walls, boxes, dir)
                if (!can) return false to emptySet()
                q += items
            }

            return true to moved
        }

        part2 {
            val (map, dirsList) = input.split("\n\n")
            val grid = map.lines().toMapGrid()
            val dirs = dirsList.lines().joinToString("").toCharArray().map { Direction.parse(it) }

            val boxes = grid.findAll { _, c -> c == 'O' }.map { Box((it.x * 2) point it.y) }.toSet()
            val walls =
                grid.findAll { _, c -> c == '#' }
                    .flatMap { listOf(Wall((it.x * 2) point it.y), Wall((it.x * 2 + 1) point it.y)) }
                    .toSet()
            val robot = grid.find { _, c -> c == '@' }!!.let { Robot(it.x * 2 point it.y) }

            dirs.fold(robot to boxes) { (robot, boxes), dir ->
                val (worked, movedBoxes) = tryPush(walls, boxes, robot, dir)
                if (worked) Robot(robot.location + dir) to (boxes - movedBoxes) + movedBoxes.map { Box(it.location + dir) } else robot to boxes
            }.second.sumOf { it.location.y * 100 + it.location.x }
        }
    }
}