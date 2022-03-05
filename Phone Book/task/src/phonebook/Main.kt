package phonebook

import java.io.File
import kotlin.math.sqrt
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun main() {
    val phoneBook = File("F:\\Data\\directory.txt").readLines().toTypedArray()
    val findList = File("F:\\Data\\find.txt").readLines()

    println("Start searching (linear search)...")
    var startTime = System.currentTimeMillis()
    var count = 0
    for (find in findList) if (linearSearch(phoneBook, find) >= 0) count++
    var timeTaken = System.currentTimeMillis() - startTime
    println ("Found $count / ${findList.size} entries. Time taken: ${stringOf(timeTaken)}" )

    println("\nStart searching (bubble sort + jump search)...")
    startTime = System.currentTimeMillis()
    var sortingTime = boobleSort(phoneBook, timeTaken * 10)  // booble search

    count = 0
    if (sortingTime > 0) {
        for (find in findList) if (jumpSearch(phoneBook, find) >= 0) count++
        timeTaken = System.currentTimeMillis() - startTime
        val searchingTime = timeTaken - sortingTime
        println ("Found $count / ${findList.size} entries. " +
                "Time taken: ${stringOf(timeTaken)}\n" +
                "Sorting time: ${stringOf(sortingTime)}\n" +
                "Searching time: ${stringOf(searchingTime)}")
    } else {
        sortingTime = System.currentTimeMillis() - startTime
        count = 0
        for (find in findList) if (linearSearch(phoneBook, find) >= 0) count++
        val timeTaken = System.currentTimeMillis() - startTime
        val searchingTime = timeTaken - sortingTime
        println ("Found $count / ${findList.size} entries. " +
                "Time taken: ${stringOf(timeTaken)}\n" +
                "Sorting time: ${stringOf(sortingTime)} - STOPPED, moved to linear search\n" +
                "Searching time: ${stringOf(searchingTime)}")
    }
}

fun nameFrom(record: String): String = record.split(" ", limit = 2).component2()

fun stringOf(timeInterval: Long) = timeInterval.toDuration(DurationUnit.MILLISECONDS)
    .toComponents { m, s, n -> "$m min. $s sec. ${n / 1000000} ms." }

fun jumpSearch(array: Array<String>, find: String, start: Int = 0, end: Int = array.lastIndex): Int {
    if (find < nameFrom(array[start])) return -1
    if (end - start > 10) {
        val step = sqrt((end - start).toDouble()).toInt()  //recursive jump search
        for (pos in start until end step step)
            if (find <= nameFrom(array[(pos + step).coerceAtMost(end)]))
                return jumpSearch(array, find, pos, (pos + step).coerceAtMost(end))
    }
    return linearSearch(array, find, start, end)
}

fun linearSearch(array: Array<String>, find: String, start: Int = 0, end: Int = array.lastIndex ): Int {
    for (i in start..end) if (find.equals(nameFrom(array[i]))) return i
    return -1
}

fun boobleSort(array: Array<String>, timeLimit: Long = 0): Long {
    val startTime = System.currentTimeMillis()
    for (len in array.size downTo 1)
        for (j in 1 until len) {
            if (timeLimit > 0 && System.currentTimeMillis() - startTime > timeLimit) return 0
            if (nameFrom(array[j - 1]) > nameFrom(array[j]))
                array[j - 1] = array[j].also { array[j] = array[j - 1] }
        }
    return System.currentTimeMillis() - startTime
}

