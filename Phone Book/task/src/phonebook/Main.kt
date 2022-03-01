package phonebook

import java.io.File
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun main() {
    val phoneBook = File("F:\\Data\\small_directory.txt").readLines().toTypedArray()
    val findList = File("F:\\Data\\small_find.txt").readLines()

    println("Start searching (linear search)...")
    var startTime = System.currentTimeMillis()

    var counter = 0
    for (searchName in findList)
        for (record in phoneBook)
            if (searchName.equals(nameFrom(record))) counter++

    var searchDuration = (System.currentTimeMillis() - startTime).toDuration(DurationUnit.MILLISECONDS)
    println ("Found $counter / ${findList.size} entries. Time taken: ${searchDuration.toStr()}" )

    println("\nStart searching (bubble sort + jump search)...")
    startTime = System.currentTimeMillis()

    for (len in phoneBook.size downTo 1)
        for (j in 1 until len)// booble sort
            if (nameFrom(phoneBook[j - 1]) > nameFrom(phoneBook[j]))
                phoneBook[j - 1] = phoneBook[j].also { phoneBook[j] = phoneBook[j - 1] }
    val sortDuration = (System.currentTimeMillis() - startTime).toDuration(DurationUnit.MILLISECONDS)

    counter = 0
    for (searchName in findList) if (jumpSearch(phoneBook, searchName) >= 0) counter++ // jumpSearch

    val totalDuration = (System.currentTimeMillis() - startTime).toDuration(DurationUnit.MILLISECONDS)
    searchDuration = totalDuration - sortDuration
    println ("Found $counter / ${findList.size} entries. Time taken: ${totalDuration.toStr()}")
    println ("Sorting time: ${sortDuration.toStr()} - STOPPED, moved to linear search")
    print ("Searching time: ${searchDuration.toStr()}")
}

fun nameFrom(record: String): String = record.split(" ", limit = 2).component2()

fun Duration.toStr() = this.toComponents { m, s, n -> "$m min. $s sec. ${n / 1000000} ms." }

fun jumpSearch(array: Array<String>, find: String, start: Int = 0, end: Int = array.lastIndex): Int {
    if (find < nameFrom(array[start])) return -1
    if (end - start > 10) {
        val step = sqrt((end - start).toDouble()).toInt()  //recursive jump search
        for (pos in start until end step step)
            if (find <= nameFrom(array[(pos + step).coerceAtMost(end)]))
                return jumpSearch(array, find, pos, (pos + step).coerceAtMost(end))
    } else for (pos in start..end) if (find.compareTo(nameFrom(array[pos])) == 0) return pos // linear search
    return -1
}

