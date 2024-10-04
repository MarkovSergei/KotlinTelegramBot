package org.example
import java.io.File

fun main() {
    val wordsFile = File("words.txt")
    wordsFile.createNewFile()
    val dictionary = mutableListOf<Word>()
    val lines: List<String> = wordsFile.readLines()
    for (line in lines) {
        val lineParts = line.split("|")
        val word = Word(
            original = lineParts[0],
            translate = lineParts[1],
            correctAnswersCount = lineParts.getOrNull(2)?.toInt() ?: 0
        )
        dictionary.add(word)
    }
    println(dictionary)
}