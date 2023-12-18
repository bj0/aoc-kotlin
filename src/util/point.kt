package util

import kotlin.math.absoluteValue


data class Point(val x: Int, val y: Int) {
    companion object {
        val Zero = 0 point 0
    }
}

data class PointL(val x: Long, val y: Long) {
    companion object {
        val Zero = 0L point 0
    }
}

infix fun Point.mdist(other: Point) = (other.x - x).absoluteValue + (other.y - y).absoluteValue
infix fun PointL.mdist(other: PointL) = (other.x - x).absoluteValue + (other.y - y).absoluteValue

operator fun Point.plus(dir: Direction) = move(dir, 1)
operator fun PointL.plus(dir: Direction) = move(dir, 1)

val Direction.point
    get() = when (this) {
        Direction.Left -> Point.Zero.left
        Direction.Up -> Point.Zero.up
        Direction.Right -> Point.Zero.right
        Direction.Down -> Point.Zero.down
    }
val Direction.pointL
    get() = when (this) {
        Direction.Left -> PointL.Zero.left
        Direction.Up -> PointL.Zero.up
        Direction.Right -> PointL.Zero.right
        Direction.Down -> PointL.Zero.down
    }

fun Point.move(dir: Direction, steps: Int = 0) = dir.point.let { p ->
    copy(x = x + steps * p.x, y = y + steps * p.y)
}
fun PointL.move(dir: Direction, steps: Int = 0) = dir.pointL.let { p ->
    copy(x = x + steps * p.x, y = y + steps * p.y)
}


infix fun Int.point(y: Int) = Point(this, y)
infix fun Long.point(y: Long) = PointL(this, y)

val Point.up get() = copy(y = (y - 1))
val Point.down get() = copy(y = (y + 1))
val Point.left get() = copy(x = (x - 1))
val Point.right get() = copy(x = (x + 1))
val PointL.up get() = copy(y = (y - 1))
val PointL.down get() = copy(y = (y + 1))
val PointL.left get() = copy(x = (x - 1))
val PointL.right get() = copy(x = (x + 1))
