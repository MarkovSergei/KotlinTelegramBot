const val START_BOT = "/start"

fun checkNextQuestionAndSend(
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: Long
) {
    val question = trainer.getNextQuestion()
    if (question == null) {
        telegramBotService.sendMessage(chatId, "Вы выучили все слова в базе")
    } else {
        telegramBotService.sendQuestion(chatId, question)
    }
}


fun main(args: Array<String>) {
    val botToken = args[0]
    var updateId: Long = 0
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val updateIdRegex: Regex = "\"update_id\":(.+?),".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+),".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()

    val telegramBotService = TelegramBotService(botToken)
    val trainer = LearnWordsTrainer()


    while (true) {
        Thread.sleep(2000)
        val updates: String = telegramBotService.getUpdates(updateId)
        //println(updates)
        val updateIdMatch = updateIdRegex.find(updates)?.groups?.get(1)?.value?.toLong() ?: continue
        updateId = updateIdMatch + 1
        val text = messageTextRegex.find(updates)?.groups?.get(1)?.value
        val data = dataRegex.find(updates)?.groups?.get(1)?.value
        val chatId = chatIdRegex.find(updates)?.groups?.get(1)?.value?.toLong() ?: continue
        println(text)

        when {
            text == START_BOT -> {
                telegramBotService.sendMenu(chatId)
            }

            data == STATISTIC_BUTTON -> {
                val statistics = trainer.getStatistic()
                val statisticsPrint =
                    "Выучено ${statistics.learnedCount} из ${statistics.totalWords} слов | ${statistics.percentLearned}%"
                telegramBotService.sendMessage(chatId, statisticsPrint)
            }

            data == LEARN_BUTTON -> {
                checkNextQuestionAndSend(
                    trainer = trainer,
                    telegramBotService = telegramBotService,
                    chatId = chatId
                )
            }

            data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true -> {
                val index = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
                if (trainer.checkAnswer(index)) {
                    telegramBotService.sendMessage(chatId, "Правильно!")
                } else {
                    telegramBotService.sendMessage(
                        chatId,
                        "Неправильно. ${trainer.question?.correctAnswer?.original} - это ${trainer.question?.correctAnswer?.translate}."
                    )
                }
                checkNextQuestionAndSend(
                    trainer = trainer,
                    telegramBotService = telegramBotService,
                    chatId = chatId
                )
            }
        }
    }
}
