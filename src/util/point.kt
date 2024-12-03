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

fun IntPoint.move(dir: Direction, steps: Int = 0) = IntPoint.Zero.step(dir).let { p ->
    copy(x = x + steps * p.x, y = y + steps * p.y)
}

fun LongPoint.move(dir: Direction, steps: Int = 0) = LongPoint.Zero.step(dir).let { p ->
    copy(x = x + steps * p.x, y = y + steps * p.y)
}

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
