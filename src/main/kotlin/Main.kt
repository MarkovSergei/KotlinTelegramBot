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