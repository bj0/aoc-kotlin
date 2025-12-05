package util

import kotlin.math.abs

// generic point stuff

interface MathScope<T> {
    val zero: T
    val one: T
    fun <N : Number> number(a: N): T
    fun add(a: T, b: T): T
    fun multiply(a: T, b: T): T
    fun divide(a: T, b: T): T
    fun unaryMinus(a: T): T

    val T.absoluteValue: T
}

interface PointScope<T, P : GPoint<T>> : MathScope<T> {
    val origin: P get() = zero point zero

    fun newPoint(x: T, y: T): P
    infix fun T.point(y: T) = newPoint(this, y)

    operator fun P.plus(p: P) = (x + p.x) point (y + p.y)
    operator fun T.times(p: P) = (this * p.x) point (this * p.y)

    fun P.step(dir: Direction): P = when (dir) {
        Direction.Left -> left
        Direction.Up -> up
        Direction.Right -> right
        Direction.Down -> down
    }

    fun P.move(dir: Direction, steps: Int = 1) = this + number(steps) * origin.step(dir)
}


context(ms: MathScope<T>)
operator fun <T> T.plus(other: T) = ms.add(this, other)

context(ms: MathScope<T>)
operator fun <T> T.minus(other: T) = this + (-other)

context(ms: MathScope<T>)
operator fun <T> T.times(other: T) = ms.multiply(this, other)

context(ms: MathScope<T>)
operator fun <T> T.div(other: T) = ms.divide(this, other)

context(ms: MathScope<T>)
operator fun <T> T.unaryMinus() = ms.unaryMinus(this)

context(ms: MathScope<T>)
operator fun <T> T.inc(): T = this + ms.one

context(ms: MathScope<T>)
operator fun <T> T.dec(): T = this - ms.one

interface GPoint<T> {
    val x: T
    val y: T
}

context(_: MathScope<T>)
infix fun <T> GPoint<T>.mdist(other: GPoint<T>) = (other.x - x).absoluteValue + (other.y - y).absoluteValue

context(_: PointScope<T, P>)
val <T, P : GPoint<T>> P.up
    get() = x point y.dec()
context(_: PointScope<T, P>)
val <T, P : GPoint<T>> P.down
    get() = x point y.inc()
context(_: PointScope<T, P>)
val <T, P : GPoint<T>> P.left
    get() = x.dec() point y
context(_: PointScope<T, P>)
val <T, P : GPoint<T>> P.right
    get() = x.inc() point y

object IntPointScope : PointScope<Int, IntPointScope.Point> {
    data class Point(override val x: Int, override val y: Int) : GPoint<Int>

    override val zero = 0
    override val one = 1

    override fun <N : Number> number(a: N) = a.toInt()

    override val Int.absoluteValue: Int
        get() = abs(this)

    override fun unaryMinus(a: Int) = -a

    override fun divide(a: Int, b: Int) = a / b

    override fun multiply(a: Int, b: Int) = a * b

    override fun add(a: Int, b: Int) = a + b

    override fun newPoint(x: Int, y: Int) = Point(x, y)
}

object LongPointScope : PointScope<Long, LongPointScope.Point> {
    data class Point(override val x: Long, override val y: Long) : GPoint<Long>

    override val zero = 0L
    override val one = 1L

    override fun <N : Number> number(a: N) = a.toLong()

    override fun newPoint(x: Long, y: Long) = Point(x, y)

    override val Long.absoluteValue: Long
        get() = abs(this)

    override fun unaryMinus(a: Long) = -a

    override fun divide(a: Long, b: Long) = a / b

    override fun multiply(a: Long, b: Long) = a * b

    override fun add(a: Long, b: Long) = a + b
}
