import java.io.File

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int,
) {
    override fun toString(): String {
        return "$original, $translate, $correctAnswersCount"
    }
}

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

    val notLearnedList = dictionary.filter { it.correctAnswersCount < 3 }
    println(notLearnedList.size)

    while (true) {
        println()
        println("Меню: ")
        println("1. Учить слова")
        println("2. Статистика")
        println("0. Выйти")
        print("Ваш выбор: ")
        val input = readln()

        when (input) {
            "1" -> {
                if (notLearnedList.isEmpty()) {
                    println("Все слова в словаре выучены.")
                    continue
                }

                val questionWords = notLearnedList.take(4).shuffled()
                val correctAnswer = questionWords[0].original
                val listAnswer = (0..3).toList().shuffled()

                println()
                println(correctAnswer)
                println("1 - ${questionWords[listAnswer[0]].translate}")
                println("2 - ${questionWords[listAnswer[1]].translate}")
                println("3 - ${questionWords[listAnswer[2]].translate}")
                println("4 - ${questionWords[listAnswer[3]].translate}")
                print("Введите номер ответа (1-4): ")

                val userAnswer = readln().toInt()

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
