package org.example

import java.io.File

fun main() {
    val wordsFile = File("words.txt")
    wordsFile.createNewFile()
    val lines = wordsFile.readLines()
    for (line in lines) {
        println(line)
    }
}