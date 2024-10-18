import java.io.File

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int,
)

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

    while (true) {
        println("Меню: ")
        println("1. Учить слова")
        println("2. Статистика")
        println("0. Выйти")
        print("Ваш выбор: ")
        val input = readln()

        when (input) {
            "1" -> {
                println("Вы нажали 1")
                while (true) {

                }
            }

            "2" -> {
                val totalWords = dictionary.size
                val learnedCount = dictionary.filter { it.correctAnswersCount >= 3 }.size
                val percentLearned = (learnedCount * 100) / totalWords
                println("Выучено $learnedCount  из $totalWords слов | $percentLearned%")
            }

            "0" -> break
            else -> println("Некорректный ввод")
        }
    }
}