@file:Suppress("FunctionName")

package util

class CachedRecursiveFunction<T, R>(internal val block: CachedRecursiveFunction<T, R>.(T) -> R) {
    private val cache = mutableMapOf<T, R>()
    fun callRecursive(input: T): R = cache.getOrPut(input) { this.block(input) }
    operator fun invoke(input: T): R = callRecursive(input)
}

fun <T, R> cachedDeepRecursiveFunction(block: suspend DeepRecursiveScope<T, R>.(T) -> R): DeepRecursiveFunction<T, R> =
    with(mutableMapOf<T, R>()) {
        DeepRecursiveFunction {
            getOrPut(it) { block(it) }
        }
    }