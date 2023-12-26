package year2023

import io.ksmt.KContext
import io.ksmt.expr.rewrite.simplify.toRealValue
import io.ksmt.solver.z3.KZ3Solver
import io.ksmt.utils.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import util.*
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

fun main() {
    listOf(Day24::math, Day24::eliz, Day24::iter, Day24::solution).solveAll(
//    listOf(KSMT::solution).solveAll(
//    listOf(Day24::test).solveAll(
//        InputProvider.raw(
//            """
//            19, 13, 30 @ -2,  1, -2
//            18, 19, 22 @ -1, -1, -2
//            20, 25, 34 @ -2, -2, -4
//            12, 31, 28 @ -1, -2, -1
//            20, 19, 15 @  1, -5, -3
//        """.trimIndent()
//        )
    )
}

object KSMT {
    // can't get this to work

    data class Stone(val pos: Point3L, val vel: Point3L)

    val solution = puzzle {
        val parser3 = lineParser { line ->
            line.split(" @ ").let { (a, b) ->
                Stone(
                    Point3L(a.getLongList()),
                    Point3L(b.getLongList())
                )
            }
        }

        part1(parser3) { stones ->
            val ctx = KContext()
            with(ctx) {
                KZ3Solver(this).use { solver ->
                    val vx by intSort
                    val vy by intSort
                    val vz by intSort
                    val x by intSort
                    val y by intSort
                    val z by intSort

                    val s1 = stones.first()
                    val s2 = stones.find { it.vel.independentOf(s1.vel) }!!
                    val s3 = stones.drop(1).find { it.vel.independentOf(s2.vel) }!!
                    listOf(s1, s2, s3).forEachIndexed { i, stone ->
                        val t = mkConst("t$i", intSort)
                        solver.assert(t gt 0.expr)
                        solver.assert((x + vx * t) eq (stone.pos.x.expr + t * stone.vel.x.expr))
                        solver.assert((y + vy * t) eq (stone.pos.y.expr + t * stone.vel.y.expr))
                        solver.assert((z + vz * t) eq (stone.pos.z.expr + t * stone.vel.z.expr))
                    }

                    solver.check(timeout = 300.seconds).debug("check:")

                    val model = solver.model()
                    listOf(model.eval(x), model.eval(y), model.eval(z)).debug().sumOf { it.toRealValue()!!.numerator }
                }
            }
        }
    }

    private fun Point3L.independentOf(b: Point3L) = cross(b).any { it != 0L }
    private infix fun Point3L.cross(other: Point3L) =
        Point3L(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)
}

object Day24 {
    val solution = puzzle {
        part1 {
            val stones = lines.map {
                it.split(" @ ").let { (a, b) ->
                    Hail(
                        Point3d(a.getLongList().map(Long::toDouble)).ignoreZ(),
                        Point3d(b.getLongList().map(Long::toDouble)).ignoreZ()
                    )
                }
            }
            val min = 200000000000000.0
            val max = 400000000000000.0
//            val (min, max) = 7.0 to 24.0
            stones.withIndex().sumOf { (i, s) ->
                stones.drop(i + 1).count { s2 ->
                    s.intersect(s2)
                        ?.let { (p, t) -> t.first > 0 && t.second > 0 && p.within(min, max) }
                        ?: false
                }
            }
        }

    }

    val eliz = puzzle {
        // roman elizarov's golf'd solution
        part2 {
            val a = lines.map { s -> s.split("@").map { vs -> vs.split(",").map { it.trim().toLong() } } }
            val tm = (0..2).firstNotNullOf { k ->
                a.indices.firstNotNullOfOrNull { i ->
                    a.indices.map { j ->
                        if (a[j][1][k] == a[i][1][k]) (if (a[j][0][k] == a[i][0][k]) 0L else -1L) else {
                            val tn = a[j][0][k] - a[i][0][k]
                            val td = a[i][1][k] - a[j][1][k]
                            if (tn % td == 0L) tn / td else -1L
                        }
                    }.takeIf { tm -> tm.all { it >= 0 } }
                }
            }
            val (i, j) = tm.withIndex().filter { it.value > 0 }.map { it.index }
            fun p(i: Int, k: Int, t: Long) = a[i][0][k] + a[i][1][k] * t
            (0..2).sumOf { k -> p(i, k, tm[i]) - (p(i, k, tm[i]) - p(j, k, tm[j])) / (tm[i] - tm[j]) * tm[i] }
        }
    }

    val math = puzzle {
        // haven't got this to work
        val parser = lineParser { line ->
            line.split(" @ ").let { (a, b) ->
                Stone(Point3L(a), Point3L(b))
            }
        }
        part2(parser) { stones ->
            val s1 = stones.first()
            val s2 = stones.find { it.vel.independentOf(s1.vel) }!!
            val s3 = (stones - s2).find { it.vel.independentOf(s1.vel) }!!

            val (r, s) = findRock(s1, s2, s3).debug()
            r.sum() / s

            // 18873412392 too low
        }
    }

    val iter = puzzle {
        part2 {
            // this works if the right initial stones are picked, and when it works, it works fast.  keep repeating until it works
            val hailStones = lines.map { it.split(" @ ").let { (p, v) -> HailStone(Point3d(p), Point3d(v)) } }


            fun CoroutineScope.tryFind(): Long? {
                val h1 = hailStones.random()
                val h2 =
                    (hailStones - h1).find {
                        (h1.vel cross it.vel).let { r -> r.x != 0.0 || r.y != 0.0 || r.z != 0.0 }
                    }!!

                var t1 = 0L
                var t2 = 0L
                var step1 = 1e12.toLong()
                var step2 = 1e12.toLong()
                var isStep1 = true

                // approximate collision times 1 and 2
                var dist = Double.POSITIVE_INFINITY
                while (isActive && abs(step1) != 0L || abs(step2) != 0L) {
                    do {
                        val prevDist = dist
                        when (isStep1) {
                            true -> t1 += step1
                            false -> t2 += step2
                        }
                        val a = h1.at(t1)
                        val b = h2.at(t2)
                        dist = hailStones.sumOf { it.distanceToLine(a, b) }
                    } while (isActive && dist < prevDist)

                    when (isStep1) {
                        true -> step1 = (step1 / -1.5).toLong()
                        false -> step2 = (step2 / -1.5).toLong()
                    }
                    isStep1 = !isStep1
                }

                // picked bad ponits
                if (dist > 1e6) return null

                // approximated collision times are 1 off -> do final refinement
                var bestDist = dist
                for (s1 in t1 - 10L..t1 + 10L) {
                    for (s2 in t2 - 10L..t2 + 10L) {
                        val a = h1.at(s1)
                        val b = h2.at(s2)
                        val d = hailStones.sumOf { it.distanceToLine(a, b) }
                        if (d < bestDist) {
                            bestDist = d
                            t1 = s1
                            t2 = s2
                        }
                    }
                }

                // picked bad ponits
                if (bestDist > 10.0) return null

                // compute hailstone positions at t1 and t2 and derive rock start pos and velocity from that
                val ht1 = h1.at(t1)
                val ht2 = h2.at(t2)
                val rockVel = (ht2 - ht1) / (t2 - t1).toDouble()
                val rockPos = ht1 - rockVel * t1
                return (rockPos.x + rockPos.y + rockPos.z).toLong()
            }

            runBlocking {
                for (i in 0..10) {
                    println("try $i")
                    val ret = withTimeoutOrNull(100.milliseconds) {
                        tryFind()
                    }
                    if (ret != null)
                        return@runBlocking ret
                }
            }
        }
    }

    private fun lin(r: Long, a: Point3L, s: Long, b: Point3L, t: Long, c: Point3L): Point3L {
        val x = r * a.x + s * b.x + t * c.x
        val y = r * a.y + s * b.y + t * c.y
        val z = r * a.z + s * b.z + t * c.z
        return Point3L(x, y, z)
    }

    private fun findRock(h1: Stone, h2: Stone, h3: Stone): Pair<Point3L, Long> {
        val (a, A) = findPlane(h1, h2)//.debug("a")
        val (b, B) = findPlane(h1, h3)//.debug("b")
        val (c, C) = findPlane(h2, h3)//.debug("c")

        val W = lin(A, b cross c, B, c cross a, C, a cross b)//.debug("W")
        val t = a dot (b cross c)
        val w = Point3L(W.x / t, W.y / t, W.z / t)//.debug("w")

        val w1 = h1.vel - w
        val w2 = h2.vel - w
        val ww = w1 cross w2

        val E = ww dot (h2.pos cross w2)
        val F = ww dot (h1.pos cross w1)
        val G = h1.pos dot ww
        val S = ww dot ww

        val rock = lin(E, w1, -F, w2, G, ww)
        return rock to S
    }

    private fun findPlane(h1: Stone, h2: Stone): Pair<Point3L, Long> {
        val p01 = h1.pos - h2.pos
        val v01 = h1.vel - h2.vel
        val vv = h1.vel cross h2.vel
        return (p01 cross v01) to (p01 dot vv)
    }

    data class Point3d(val x: Double, val y: Double, val z: Double) {
        companion object {
            operator fun invoke(pts: List<Double>) = Point3d(pts[0], pts[1], pts[2])
        }

        override fun toString() = "($x,$y,$z)"
    }

    private infix fun Point3L.dot(other: Point3L) = x * other.x + y * other.y + z * other.z
    private infix fun Point3L.cross(other: Point3L) =
        Point3L(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)

    operator fun Point3L.minus(b: Point3L) = copy(x = x - b.x, y = y - b.y, z = z - b.z)
    private fun Point3L.independentOf(b: Point3L) = cross(b).any { it != 0L }
    data class Point2d(val x: Double, val y: Double)

    private fun Point3d.ignoreZ() = Point2d(x, y)

    data class Hail(val pos: Point2d, val vel: Point2d)

    private val Hail.ac get() = vel.y / vel.x to pos.y - vel.y / vel.x * pos.x
    private fun Hail.intersect(other: Hail): Pair<Point2d, Pair<Double, Double>>? {
        val (a, c) = ac
        val (b, d) = other.ac
        if (abs(a - b) < .001) return null
        val x = (d - c) / (a - b)
        val t0 = (x - pos.x) / vel.x
        val t1 = (x - other.pos.x) / other.vel.x
        return Point2d(x, a * (d - c) / (a - b) + c) to (t0 to t1)
    }

    private fun Point2d.within(min: Double, max: Double) = (x in min..max) && (y in min..max)

    private operator fun Point3d.times(t: Long) = Point3d(x * t, y * t, z * t)
    private operator fun Point3d.plus(dx: Point3d) = Point3d(x + dx.x, y + dx.y, z + dx.z)

    private operator fun Point3d.minus(dx: Point3d) = Point3d(x - dx.x, y - dx.y, z - dx.z)

    private operator fun Point3d.div(n: Double) = Point3d(x / n, y / n, z / n)
    data class HailStone(val pos: Point3d, val vel: Point3d) {
        fun at(t: Long) = pos + vel * t

        fun distanceToLine(l1: Point3d, l2: Point3d): Double {
            val e1 = l2 - l1
            val e2 = vel
            val n = e1 cross e2
            return abs(n dot (l1 - pos)) / n.length()
        }
    }

    private infix fun Point3d.dot(other: Point3d) = x * other.x + y * other.y + z * other.z
    infix fun Point3d.cross(b: Point3d) = Point3d(y * b.z - z * b.y, z * b.x - x * b.z, x * b.y - y * b.x)

    private fun Point3d.length() = sqrt(this dot this)

    data class Stone(val pos: Point3L, val vel: Point3L)

    private fun Point3d(str: String, delim: Char = ','): Point3d {
        val (x, y, z) = str.split(delim).filter { it.isNotBlank() }.map { it.trim().toDouble() }
        return Point3d(x, y, z)
    }
}


/**
 * from reddit:
 *
 *
 *
 *  Part 2 without any guessing / brute-forcing. Just 3D vector geometry (cross products are magic): paste
 *
 * For part 2 the problem is greatly overspecified (by which I mean, just the existence of any solution for rock's position + velocity is far from given and would be unique given just 3 independent hailstones). It's possible to just compute the solution directly with some (a lot) of vector math.
 *
 * Choose 3 hailstones such that their velocity vectors are linearly independent. Call them (p1, v1), (p2, v2), and (p3, v3).
 *
 * If the rock starts at r with velocity w, the condition that it hits hailstone i at some time t is:
 *
 * r + t*w = pi + vi*t
 *
 * r = pi + (vi-w)*t
 *
 * So another way to look at this is that we want to apply a constant adjustment "-w" to the hailstone velocities that make all the (pi, vi) rays go through a single common point. For rays in 3D it's a fairly special condition that two of them have any point in common. Also from here we'll forget about the "ray" part and just deal with lines (since we end up finding just one solution...it has to be the right one)
 *
 * For two lines (p1, v1) and (p2, v2) with (v1 x v2) != 0 (independent); a necessary and sufficient condition for them to have an intersection point is that (p1 - p2) . (v1 x v2) = 0. If we consider what values of "w" can be applied to v1 and v2 to make that happen:
 *
 * Let (p1 - p2) . (v1 x v2) = M
 *
 * (v1 - w) x (v2 - w) = (v1 x v2) - ((v1 - v2) x w)
 *
 * dot both sides with (p1 - p2). On the left we get the "adjusted" condition which we want to equal 0. On the right the first term becomes M:
 *
 * 0 = M - (p1 - p2) . ((v1 - v2) x w)
 *
 * IOW we need to choose w s.t. (p1 - p2) . ((v1 - v2) x w) = M
 *
 * Using the definition of triple scalar product to rearrange, we get w . ((p1 - p2) x (v1 - v2)) = M as the condition on w. Zooming out, this equation is of form w . a = A : that is an equation for a plane.
 *
 * To narrow down w to a single point, we need three such planes, so do the same thing with (p1, p3) and (p2, p3) to get three equations w.a = A, w.b = B, w.c = C.
 *
 * Assuming (check if needed) that a, b, c are independent, we can just write w = p*(bxc) + q*(cxa) + r*(axb) as a general form, and then plug that in to the three equations above to find: A = w.a = p*a.(bxc), B = w.b = q*b.(cxa), C = w.c = r*c.(axb)
 *
 * It's easy to solve for p,q,r here to directly find the value of w. Here we can also make use of the given that w is an integer point: we'll need to divide for the first time here (by a.(bxc)) and can just round to the nearest integer to correct for any floating point imprecision.
 *
 * Now we know w, it is easy to apply that velocity adjustment to p1 and p2 and find where their intersection point is (exercise for reader or just look at the code...this is part1 but in 3D), and that's the solution for where the rock starts.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 * import argparse
 * import collections
 * import sys
 *
 * def main(filename):
 *     lines = read_file(filename)
 *     task = parse_task(lines)
 *     answer = solve(*task)
 *
 *     print(answer)
 *
 * def read_file(filename):
 *     with open(filename, "r") as f:
 *         lines = [line.strip() for line in f]
 *     return lines
 *
 * Vec3 = collections.namedtuple("Vec3", "x,y,z", defaults = (0, 0, 0))
 *
 * def parse_task(lines):
 *     pts = []
 *     vels = []
 *     for line in lines:
 *         p, _, v = line.partition("@")
 *         pts.append(Vec3(*map(int, p.split(","))))
 *         vels.append(Vec3(*map(int, v.split(","))))
 *     return (pts, vels)
 *
 * def solve(pts, vels):
 *     n = len(pts)
 *
 *     p1, v1 = pts[0], vels[0]
 *     for i in range(1, n):
 *         if indep(v1, vels[i]):
 *             p2, v2 = pts[i], vels[i]
 *             break
 *     for j in range(i+1, n):
 *         if indep(v1, vels[j]) and indep(v2, vels[j]):
 *             p3, v3 = pts[j], vels[j]
 *             break
 *
 *     rock, S = find_rock(p1, v1, p2, v2, p3, v3)
 *     return sum(rock) / S
 *
 * def find_rock(p1, v1, p2, v2, p3, v3):
 *     a, A = find_plane(p1, v1, p2, v2)
 *     b, B = find_plane(p1, v1, p3, v3)
 *     c, C = find_plane(p2, v2, p3, v3)
 *
 *     w = lin(A, cross(b, c), B, cross(c, a), C, cross(a, b))
 *     t = dot(a, cross(b, c))
 *     # given that w is integer, so force it here to avoid carrying through
 *     # imprecision
 *     # rest of the computation is integer except the final division
 *     w = Vec3(round(w.x / t), round(w.y / t), round(w.z / t))
 *     print(w)
 *
 *     w1 = sub(v1, w)
 *     w2 = sub(v2, w)
 *     ww = cross(w1, w2)
 *
 *     E = dot(ww, cross(p2, w2))
 *     F = dot(ww, cross(p1, w1))
 *     G = dot(p1, ww)
 *     S = dot(ww, ww)
 *
 *     rock = lin(E, w1, -F, w2, G, ww)
 *     return (rock, S)
 *
 * def find_plane(p1, v1, p2, v2):
 *     p12 = sub(p1, p2)
 *     v12 = sub(v1, v2)
 *     vv = cross(v1, v2)
 *     return (cross(p12, v12), dot(p12, vv))
 *
 * def cross(a, b):
 *     return Vec3(a.y*b.z - a.z*b.y, a.z*b.x - a.x*b.z, a.x*b.y - a.y*b.x)
 *
 * def dot(a, b):
 *     return a.x*b.x + a.y*b.y + a.z*b.z
 *
 * def sub(a, b):
 *     return Vec3(a.x-b.x, a.y-b.y, a.z-b.z)
 *
 * def lin(r, a, s, b, t, c):
 *     x = r*a.x + s*b.x + t*c.x
 *     y = r*a.y + s*b.y + t*c.y
 *     z = r*a.z + s*b.z + t*c.z
 *     return Vec3(x, y, z)
 *
 * def indep(a, b):
 *     return any(v != 0 for v in cross(a, b))
 *
 *
 * if __name__ == "__main__":
 *     parser = argparse.ArgumentParser()
 *     parser.add_argument("filename", nargs="?", default="input.txt")
 *     args = parser.parse_args()
 *     sys.exit(main(args.filename))
 *
 */