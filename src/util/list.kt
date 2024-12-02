package util

fun <T> List<T>.omit(index:Int) = subList(0, index) + subList(index + 1, size)