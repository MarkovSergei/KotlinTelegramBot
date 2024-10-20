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



    while (true) {
        println("\nМеню: ")
        println("1. Учить слова")
        println("2. Статистика")
        println("0. Выйти")
        print("Ваш выбор: ")
        val input = readln()

        when (input) {


            "1" -> {
                while (true) {
                    val notLearnedList = dictionary.filter { it.correctAnswersCount < 3 }
                    if (notLearnedList.isEmpty()) {
                        println("Все слова в словаре выучены.")
                        continue
                    }

                    val questionWords = notLearnedList.shuffled().take(4)
                    val correctAnswer = questionWords[0].original

                    val variants = questionWords
                        .shuffled()
                        .mapIndexed { index: Int, word: Word -> " ${index + 1} – ${word.translate}" }
                        .joinToString(
                            separator = "\n",
                            prefix = "\n${correctAnswer}\n",
                            postfix = "\n 0 – выйти в меню",
                        )

                    println(variants)
                    print("Введите ответ: ")
                    var userAnswer = readln().toIntOrNull()

                    if (userAnswer == 0) break

                    while (userAnswer == null || userAnswer !in 0..4) {
                        println("Неверный ввод. Выберите правильный ответ: ")
                        println(variants)
                        print("Введите номер ответа (1-4): ")
                        userAnswer = readln().toIntOrNull()
                    }
                    println("\nправильно")
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
