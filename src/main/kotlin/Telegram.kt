fun main(args: Array<String>) {
    val botToken = args[0]
    var updateId = 0
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val updateIdRegex: Regex = "\"update_id\":(.+?),".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+),".toRegex()

    val telegramBotService = TelegramBotService(botToken)

    while (true) {
        Thread.sleep(2000)
        val updates: String = telegramBotService.getUpdates(updateId)
        //println(updates)
        val updateIdMatch = updateIdRegex.find(updates)?.groups?.get(1)?.value?.toInt() ?: continue
        updateId = updateIdMatch + 1
        val text = messageTextRegex.find(updates)?.groups?.get(1)?.value
        val chatId = chatIdRegex.find(updates)?.groups?.get(1)?.value?.toInt() ?: continue
        println(text)

        if (text == "Hello") telegramBotService.sendMessage(chatId, "Hello")
    }
}


