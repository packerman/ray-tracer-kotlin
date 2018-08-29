package raytracer.utils

fun <T> Iterable<T>.second(): T {
    val iterator = iterator()
    check(iterator.hasNext()) { "Collection is empty." }
    iterator.next()
    check(iterator.hasNext()) { "Collection has only one element." }
    return iterator.next()
}

