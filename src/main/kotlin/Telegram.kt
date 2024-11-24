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

        //СТАТИСТИКА
        val statistics = trainer.getStatistic()
        val statisticsPrint =
            "Выучено ${statistics.learnedCount} из ${statistics.totalWords} слов | ${statistics.percentLearned}%"
        if (text == "/start") telegramBotService.sendMenu(chatId)
        if (data == STATISTIC_BUTTON) telegramBotService.sendMessage(chatId, statisticsPrint)

        //ТРЕНАЖЕР
        val question = trainer.getNextQuestion()

        fun checkNextQuestionAndSend() {
            if (data == LEARN_BUTTON) {
                if (question == null) {
                    telegramBotService.sendMessage(chatId, "Вы выучили все слова в базе")
                } else {
                    telegramBotService.sendQuestion(chatId, question)

                    val answerPrefix = CALLBACK_DATA_ANSWER_PREFIX

                    if (data.startsWith(answerPrefix)) {
                        val index = data.substringAfter(answerPrefix).toInt()
                        println(index)
                    } else {
                        println("Что не так делаю?")
                    }
                }
            }
        }

        checkNextQuestionAndSend()


    }
}
