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

        if (text == "/start") telegramBotService.sendMenu(chatId)
        if (data == STATISTIC_BUTTON) telegramBotService.sendMessage(chatId, "Выучено 10 слов из 10 | 100%")
    }
}