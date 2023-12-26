package util

import kotlin.math.absoluteValue


data class Point(val x: Int, val y: Int) {
    companion object {
        val Zero = 0 point 0
        operator fun invoke(pts: List<Int>) = Point(pts[0], pts[1])
        operator fun invoke(line: String): Point = Point(line.getIntList())
    }

    override fun toString() = "($x,$y)"
}

data class PointL(val x: Long, val y: Long) {
    companion object {
        val Zero = 0L point 0
        operator fun invoke(pts: List<Long>) = PointL(pts[0], pts[1])
        operator fun invoke(line: String): PointL = PointL(line.getLongList())
    }

    override fun toString() = "($x,$y)"
}

data class Point3(val x: Int, val y: Int, val z: Int) {
    companion object {
        operator fun invoke(pts: List<Int>) = Point3(pts[0], pts[1], pts[2])
        operator fun invoke(line: String): Point3 = Point3(line.getIntList())
    }

    override fun toString() = "($x,$y,$z)"
}

data class Point3L(val x: Long, val y: Long, val z: Long): Iterable<Long> by listOf(x, y, z) {
    companion object {
        operator fun invoke(pts: List<Long>) = Point3L(pts[0], pts[1], pts[2])
        operator fun invoke(line: String): Point3L = Point3L(line.getLongList())
    }

    override fun toString() = "($x,$y,$z)"
}


operator fun Point3.rangeTo(other: Point3) = sequence {
    for (i in x..other.x) {
        for (j in y..other.y) {
            for (k in z..other.z) {
                yield(Point3(i, j, k))
            }
        }
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

fun Point.within(width: Int, height: Int) = x in 0..<width && y in 0..<height

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
