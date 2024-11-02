import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {
    val botToken = args[0]
    var updateId = 0

    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updateId)
       println(updates)

        val updateIdRegex: Regex = "\"update_id\":(.+?),".toRegex()
        val updateIdMatch = updateIdRegex.find(updates)?.groups?.get(1)?.value?.toInt() ?: continue

        updateId = updateIdMatch + 1

        val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
        val text = messageTextRegex.find(updates)?.groups?.get(1)?.value
        println(text)

    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val client: HttpClient = HttpClient.newBuilder().build()
    val requestGetUpdates: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val responseGetUpdates: HttpResponse<String> = client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString())
    return responseGetUpdates.body()
}