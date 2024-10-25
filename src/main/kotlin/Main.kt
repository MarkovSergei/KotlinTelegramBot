data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int,
) {
    override fun toString(): String {
        return "$original, $translate, $correctAnswersCount"
    }
}


fun questionToString(question: Question): String {
    val variants = question.variants
        .mapIndexed { index: Int, word: Word -> " ${index + 1} – ${word.translate}" }
        .joinToString(separator = "\n")
    return question.correctAnswer.questionWord + "\n" + variants + "\n 0 - выход в меню"
}


fun main() {
    val trainer = LearnWordsTrainer()

    while (true) {
        println("\nМеню: ")
        println("1. Учить слова")
        println("2. Статистика")
        println("0. Выйти")
        print("Ваш выбор: ")
        val input = readln()

        when (input) {
            "1" -> {
                val question = trainer.getNextQuestion()
                while (true) {

                    if (question == null) {
                        println("Все слова в словаре выучены.")
                        break
                    } else {
                        println(questionToString(question))
                        print("Введите ответ: ")

                        var userAnswer = readln().toIntOrNull()
                        if (userAnswer == 0) break



                        while (userAnswer == null || userAnswer !in 0..4) {
                            println("Неверный ввод. Выберите правильный ответ: ")
                            println(questionToString(question))
                            print("Введите номер ответа (1-4): ")
                            userAnswer = readln().toIntOrNull()
                        }
                        if (trainer.checkAnswer(userAnswer.minus(1))) {
                            println("Правильно!")
                        } else {
                            println("Неправильно. ${question.correctAnswer.original} - это ${question.correctAnswer.translate}.")
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
