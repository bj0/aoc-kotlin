package util

enum class GridDirection(val dx: Int, val dy: Int) {
    North(0, -1),
    NorthEast(1, -1),
    East(1, 0),
    SouthEast(1, 1), South(0, 1),
    SouthWest(-1, 1),
    West(-1, 0),
    NorthWest(-1, -1);

    operator fun component1() = dx
    operator fun component2() = dy
}

enum class Direction(var char: Char) {
    Left('L'),
    Up('U'),
    Right('R'),
    Down('D'),
    ;

    fun clockwise(): Direction {
        return entries[(ordinal + 1) % entries.size]
    }

    fun counterClockwise(): Direction {
        return entries[(ordinal + entries.size - 1) % entries.size]
    }

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