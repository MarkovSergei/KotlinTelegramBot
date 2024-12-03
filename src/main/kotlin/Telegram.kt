import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    val botToken = args[0]
    var lastUpdateId: Long = 0
    val json = Json {
        ignoreUnknownKeys = true
    }

    val telegramBotService = TelegramBotService(botToken)
    val trainer = LearnWordsTrainer()

    while (true) {
        Thread.sleep(2000)
        val responseString: String = telegramBotService.getUpdates(lastUpdateId)
        val response: Response = json.decodeFromString(responseString)
        val updates = response.result
        val firstUpdate = updates.firstOrNull() ?: continue
        val updateId = firstUpdate.updateId
        lastUpdateId = updateId + 1
        val message = firstUpdate.massage?.text
        val data = firstUpdate.callBackQuery?.data
        val chatId = firstUpdate.massage?.chat?.id ?: firstUpdate.callBackQuery?.message?.chat?.id

        when {
            message == START_BOT -> {
                if (chatId != null) {
                    telegramBotService.sendMenu(json, chatId)
                }
            }

            data == STATISTIC_BUTTON -> {
                val statistics = trainer.getStatistic()
                val statisticsPrint =
                    "Выучено ${statistics.learnedCount} из ${statistics.totalWords} слов | ${statistics.percentLearned}%"
                telegramBotService.sendMessage(json, chatId, statisticsPrint)
            }

            data == LEARN_BUTTON -> {
                checkNextQuestionAndSend(
                    json = Json,
                    trainer = trainer,
                    telegramBotService = telegramBotService,
                    chatId = chatId
                )
            }

            data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true -> {
                val index = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
                if (trainer.checkAnswer(index)) {
                    telegramBotService.sendMessage(json, chatId, "Правильно!")
                } else {
                    telegramBotService.sendMessage(
                        json,
                        chatId,
                        "Неправильно. ${trainer.question?.correctAnswer?.original} - это ${trainer.question?.correctAnswer?.translate}."
                    )
                }
                checkNextQuestionAndSend(
                    json = Json,
                    trainer = trainer,
                    telegramBotService = telegramBotService,
                    chatId = chatId
                )
            }
        }
    }
}
