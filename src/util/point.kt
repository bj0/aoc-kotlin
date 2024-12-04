package util

import kotlin.math.absoluteValue

// old point stuff

data class IntPoint(val x: Int, val y: Int) {
    companion object {
        val Zero = 0 point 0
    }

    override fun toString() = "($x,$y)"
}

data class LongPoint(val x: Long, val y: Long) {
    companion object {
        val Zero = 0L point 0
        val North = Zero.North
        val NorthEast = Zero.NorthEast
        val East = Zero.East
        val SouthEast = Zero.SouthEast
        val South = Zero.South
        val SouthWest = Zero.SouthWest
        val West = Zero.West
        val NorthWest = Zero.NorthWest
    }

    override fun toString() = "($x,$y)"
}

data class IntPoint3(val x: Int, val y: Int, val z: Int) {
    companion object {
        operator fun invoke(pts: List<Int>) = IntPoint3(pts[0], pts[1], pts[2])
    }

    override fun toString() = "($x,$y,$z)"
}

data class LongPoint3(val x: Long, val y: Long, val z: Long) : Iterable<Long> by listOf(x, y, z) {
    companion object {
        operator fun invoke(pts: List<Long>) = LongPoint3(pts[0], pts[1], pts[2])
        operator fun invoke(line: String): LongPoint3 = LongPoint3(line.getLongList())
    }

    override fun toString() = "($x,$y,$z)"
}


operator fun IntPoint3.rangeTo(other: IntPoint3) = sequence {
    for (i in x..other.x) {
        for (j in y..other.y) {
            for (k in z..other.z) {
                yield(IntPoint3(i, j, k))
            }
        }
    }
}


infix fun IntPoint.mdist(other: IntPoint) = (other.x - x).absoluteValue + (other.y - y).absoluteValue
infix fun LongPoint.mdist(other: LongPoint) = (other.x - x).absoluteValue + (other.y - y).absoluteValue

operator fun IntPoint.plus(dir: Direction) = move(dir, 1)
operator fun LongPoint.plus(dir: Direction) = move(dir, 1)

operator fun Int.times(point: IntPoint) = IntPoint(point.x * this, point.y * this)
operator fun Long.times(point: LongPoint) = LongPoint(point.x * this, point.y * this)

operator fun IntPoint.plus(other: IntPoint) = IntPoint(this.x + other.x, this.y + other.y)
operator fun LongPoint.plus(other: LongPoint) = LongPoint(this.x + other.x, this.y + other.y)

operator fun IntPoint.minus(other: IntPoint) = IntPoint(this.x - other.x, this.y - other.y)
operator fun LongPoint.minus(other: LongPoint) = LongPoint(this.x - other.x, this.y - other.y)

fun IntPoint.step(dir: Direction) = when (dir) {
    Direction.Left -> left
    Direction.Up -> up
    Direction.Right -> right
    Direction.Down -> down
}

fun LongPoint.step(dir: Direction) = when (dir) {
    Direction.Left -> left
    Direction.Up -> up
    Direction.Right -> right
    Direction.Down -> down
}

fun LongPoint.neighbors(includeDiagonals: Boolean = false) =
    if (includeDiagonals) GridDirection.entries.map { step(it) } else
        listOf(North, East, South, West)

fun LongPoint.Companion.neighbors(includeDiagonals: Boolean = false) = Zero.neighbors(includeDiagonals)

fun LongPoint.step(direction: GridDirection) = move(direction)

fun LongPoint.move(direction: GridDirection, steps: Int = 1) = direction.let { (dx, dy) ->
    copy(x = x + steps * dx, y = y + steps * dy)
}

fun LongPoint.walk(direction: GridDirection) = generateSequence(this) { it.step(direction) }


fun IntPoint.move(dir: Direction, steps: Int = 1) = IntPoint.Zero.step(dir).let { p ->
    copy(x = x + steps * p.x, y = y + steps * p.y)
}

fun LongPoint.move(dir: Direction, steps: Int = 1) = LongPoint.Zero.step(dir).let { p ->
    copy(x = x + steps * p.x, y = y + steps * p.y)
}

val LongPoint.North get() = step(GridDirection.North)
val LongPoint.NorthEast get() = step(GridDirection.NorthEast)
val LongPoint.East get() = step(GridDirection.East)
val LongPoint.SouthEast get() = step(GridDirection.SouthEast)
val LongPoint.South get() = step(GridDirection.South)
val LongPoint.SouthWest get() = step(GridDirection.SouthWest)
val LongPoint.West get() = step(GridDirection.West)
val LongPoint.NorthWest get() = step(GridDirection.NorthWest)

//fun LongPoint.walk(dir: Direction) = generateSequence(this) { p -> p.move(dir) }

fun IntPoint.within(width: Int, height: Int) = x in 0..<width && y in 0..<height

infix fun Int.point(y: Int) = IntPoint(this, y)
infix fun Long.point(y: Long) = LongPoint(this, y)

val IntPoint.up get() = copy(y = (y - 1))
val IntPoint.down get() = copy(y = (y + 1))
val IntPoint.left get() = copy(x = (x - 1))
val IntPoint.right get() = copy(x = (x + 1))
val LongPoint.up get() = copy(y = (y - 1))
val LongPoint.down get() = copy(y = (y + 1))
val LongPoint.left get() = copy(x = (x - 1))
val LongPoint.right get() = copy(x = (x + 1))
