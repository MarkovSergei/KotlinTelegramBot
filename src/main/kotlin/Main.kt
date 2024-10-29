data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int,
) {
    override fun toString(): String {
        return "$original, $translate, $correctAnswersCount"
    }
}

fun Question.asConsoleString(): String {
    val variants = this.variants
        .mapIndexed { index: Int, word: Word -> " ${index + 1} – ${word.translate}" }
        .joinToString(
            separator = "\n",
            prefix = "\n${this.correctAnswer.original}\n",
            postfix = "\n ----------\n 0 - Меню",
        )
    return variants
}

fun main() {

    val trainer = try {
        LearnWordsTrainer(3, 4)
    } catch (e: Exception) {
        println("Невозможно загрузить словарь.")
        return
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
                    val question = trainer.getNextQuestion()
                    if (question == null) {
                        println("Все слова в словаре выучены.")
                        break
                    } else {
                        println(question.asConsoleString())
                        print("Введите ответ: ")

                        val userAnswer: Int? = readln().toIntOrNull()
                        if (userAnswer == 0) break
                        if (userAnswer != null) {
                            if (trainer.checkAnswer(userAnswer.minus(1))) {
                                println("Правильно!")
                            } else {
                                println("Неправильно. ${question.correctAnswer.original} - это ${question.correctAnswer.translate}.")
                            }
                        }
                    }
                }
            }

            "2" -> {
                val statistics = trainer.getStatistic()
                println("Выучено ${statistics.learnedCount}  из ${statistics.totalWords} слов | ${statistics.percentLearned}%")
            }

            "0" -> break
            else -> println("Некорректный ввод")
        }
    }
}
