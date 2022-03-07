package phonebook

import java.io.File
import kotlin.math.sqrt
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun main() {
    val findList = File("F:\\Data\\find.txt").readLines()
    val unsortedBook = File("F:\\Data\\directory.txt").readLines().toTypedArray()

    println("Start searching (linear search)...")
    val linearSearchStartTime = System.currentTimeMillis()
    val linearSearchCount = findList.count { linearSearch(unsortedBook, it) >= 0 }
    val linearSearchDuration = System.currentTimeMillis() - linearSearchStartTime
    println ("Found $linearSearchCount / ${findList.size} entries. Time taken: ${linearSearchDuration.toTimeStr()}")

    println("\nStart searching (bubble sort + jump search)...")
    val boobleSortedBook = unsortedBook.copyOf()
    val boobleSortStartTime = System.currentTimeMillis()
    val interrupted = boobleSort(boobleSortedBook, linearSearchDuration * 10)  // booble search
    var boobleSortDuration = System.currentTimeMillis() - boobleSortStartTime
    if (interrupted) {
        val jumpSearchCount = findList.count { jumpSearch(boobleSortedBook, it) >= 0 }
        val jumpSearchDuration = System.currentTimeMillis() - boobleSortStartTime - boobleSortDuration
        println ("Found $jumpSearchCount / ${findList.size} entries. " +
                "Time taken: ${(jumpSearchDuration + boobleSortDuration).toTimeStr()}\n" +
                "Sorting time: ${boobleSortDuration.toTimeStr()}\n" +
                "Searching time: ${jumpSearchDuration.toTimeStr()}")
    } else {
        val linearSearchStartTime = System.currentTimeMillis()
        val linearSearchCount = findList.count { linearSearch(boobleSortedBook, it) >= 0 }
        val linearSearchDuration = System.currentTimeMillis() - linearSearchStartTime
        println ("Found $linearSearchCount / ${findList.size} entries. " +
                "Time taken: ${(linearSearchDuration + boobleSortDuration).toTimeStr()}\n" +
                "Sorting time: ${boobleSortDuration.toTimeStr()} - STOPPED, moved to linear search\n" +
                "Searching time: ${linearSearchDuration.toTimeStr()}")
    }

    println("\nStart searching (quick sort + binary search)...")
    val quickSortedBook = unsortedBook.copyOf()
    val quickSortStartTime = System.currentTimeMillis()
    quickSort(quickSortedBook)
    val quickSortDuration = System.currentTimeMillis() - quickSortStartTime
    val binarySearchCounter = findList.count { binarySearch(quickSortedBook, it) >= 0 }
    val binarySearchDuration = System.currentTimeMillis() - quickSortStartTime - quickSortDuration
    println ("Found $binarySearchCounter / ${findList.size} entries. " +
            "Time taken: ${(quickSortDuration + binarySearchDuration).toTimeStr()}\n" +
            "Sorting time: ${quickSortDuration.toTimeStr()}\n" +
            "Searching time: ${binarySearchDuration.toTimeStr()}")

    println("\nStart searching (hash table)...")
    val createHashStartTime = System.currentTimeMillis()
    val hashTable = hashTableOf(unsortedBook)
    val createHashDuration = System.currentTimeMillis() - createHashStartTime
    val hashTableSearchCounter = findList.count { hashTable[hashOf(it)].any { idx -> getName(unsortedBook[idx]) == it  }}
    val hashSearchDuration = System.currentTimeMillis() - createHashStartTime - createHashDuration
    println ("Found $hashTableSearchCounter / ${findList.size} entries. " +
            "Time taken: ${(createHashDuration + hashSearchDuration).toTimeStr()}\n" +
            "Creating time: ${createHashDuration.toTimeStr()}\n" +
            "Searching time: ${hashSearchDuration.toTimeStr()}")
}

fun getName(record: String): String = record.split(" ", limit = 2).component2()

fun Long.toTimeStr() = this.toDuration(DurationUnit.MILLISECONDS)
    .toComponents { m, s, n -> "$m min. $s sec. ${n / 1000000} ms." }

fun linearSearch(array: Array<String>, find: String, start: Int = 0, end: Int = array.lastIndex ): Int {
    for (i in start..end) if (find.equals(getName(array[i]))) return i
    return -1
}

fun boobleSort(array: Array<String>, timeLimit: Long = 0): Boolean {
    val startTime = System.currentTimeMillis()
    for (len in array.size downTo 1)
        for (j in 1 until len) {
            if (timeLimit > 0 && System.currentTimeMillis() - startTime > timeLimit) return false
            if (getName(array[j - 1]) > getName(array[j]))
                array[j - 1] = array[j].also { array[j] = array[j - 1] }
        }
    return true
}

fun jumpSearch(array: Array<String>, find: String, start: Int = 0, end: Int = array.lastIndex): Int {
    if (find < getName(array[start])) return -1
    if (end - start > 10) {
        val step = sqrt((end - start).toDouble()).toInt()  //recursive jump search
        for (pos in start until end step step)
            if (find <= getName(array[(pos + step).coerceAtMost(end)]))
                return jumpSearch(array, find, pos, (pos + step).coerceAtMost(end))
    }
    return linearSearch(array, find, start, end)
}

fun quickSort(array: Array<String>, start: Int = 0, end: Int = array.lastIndex) {
    if (end <= start) return
    val pivot = array[start]
    var bottom = start
    var top = end
    do {
        while (array[top] > pivot && bottom < top) top--
        if (top == bottom) break
        while (array[bottom] <= pivot && bottom < top) bottom++
        if (top == bottom) break
        array[bottom] = array[top].also { array[top] = array[bottom] }
    } while (bottom < top)
    array[start] = array[bottom].also { array[bottom] = array[start] }
    quickSort(array, start, bottom - 1)
    quickSort(array, bottom + 1, end)
}

fun binarySearch(array: Array<String>, find: String, start: Int = 0, end: Int = array.lastIndex): Int {
    if (start == end) return if (find == getName(array[start])) start else -1
    val middle = start + (end - start) / 2
    return when (find.compareTo(getName(array[middle]))) {
        1 -> binarySearch(array, find, middle + 1, end)
        -1 -> binarySearch(array, find, start, middle - 1)
        else -> middle
    }
}

fun hashOf(string: String, mod: Int = 1000): Int {
    var hash = 0
    string.forEach { hash = (hash * 255 + it.code) % mod }
    return hash
}

fun hashTableOf(array: Array<String>, mod: Int = 1000): Array<MutableList<Int>> {
    val hashTable = Array<MutableList<Int>>(mod) { mutableListOf() }
    array.forEachIndexed { index, string ->  hashTable[hashOf(getName(string))].add(index)}
    return hashTable
}