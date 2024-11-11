import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

class TelegramBotService(private val botToken: String) {
    private val apiUrl = "https://api.telegram.org/bot${this.botToken}"
    private val client: HttpClient = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "$apiUrl/getUpdates?offset=$updateId"
        val requestGetUpdates: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val responseGetUpdates: HttpResponse<String> =
            client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString())
        return responseGetUpdates.body()
    }

    fun sendMessage(chatId: Int, message: String): String? {
        val encoded = URLEncoder.encode(
            message,
            StandardCharsets.UTF_8
        )
        //println(encoded)

        val urlSendMessage = "$apiUrl/sendMessage?chat_id=$chatId&text=$encoded"
        val requestSendMessage: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val responseSendMessage: HttpResponse<String> =
            client.send(requestSendMessage, HttpResponse.BodyHandlers.ofString())
        println("Сообщение от бота отправлено: $encoded")
        return responseSendMessage.body()
    }

    fun sendMenu(chatId: Int): String? {
        val urlSendMessage = "$apiUrl/sendMessage?"
        val sendMenuBody = """
            {
            "chat_id": $chatId,
            "text": "Основное меню",
            "reply_markup": {
            "inline_keyboard": [
            [
            {
            "text": "Изучить слова",
            "callback_data": "learn_words_clicked"
            },
            {"text": "Статистика",
            "callback_data": "statistics_clicked"
            }
            ]
            ]
            }
            }
        """.trimIndent()
        val requestSendMessage: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .headers("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()
        val responseSendMessage: HttpResponse<String> =
            client.send(requestSendMessage, HttpResponse.BodyHandlers.ofString())
        return responseSendMessage.body()
    }
}