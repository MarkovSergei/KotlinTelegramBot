import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    val botToken = args[0]
    var lastUpdateId: Long = 0
    val json = Json { ignoreUnknownKeys = true }
    val trainers = HashMap<Long, LearnWordsTrainer>()

    val telegramBotService = TelegramBotService(botToken)

    while (true) {
        Thread.sleep(2000)
        val responseString: String = runCatching { telegramBotService.getUpdates(lastUpdateId) }.getOrNull() ?: continue
        val response: Response = json.decodeFromString(responseString)
        if (response.result.isNullOrEmpty()) continue
        val sortUpdates = response.result.sortedBy { it.updateId }
        sortUpdates.forEach { handleUpdate(it, json, trainers, telegramBotService) }
        lastUpdateId = sortUpdates.last().updateId + 1
    }
}

fun handleUpdate(
    update: Update,
    json: Json,
    trainers: HashMap<Long, LearnWordsTrainer>,
    telegramBotService: TelegramBotService
) {

    val message = update.massage?.text
    val data = update.callBackQuery?.data
    val chatId = update.massage?.chat?.id ?: update.callBackQuery?.message?.chat?.id ?: return
    val trainer = trainers.getOrPut(chatId) { LearnWordsTrainer("$chatId.txt") }

    when {
        message == START_BOT -> {
            telegramBotService.sendMenu(json, chatId)
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

        data == RESET_BUTTON -> {
            trainer.resetProgress()
            telegramBotService.sendMessage(json, chatId, "Прогресс сброшен.")
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
