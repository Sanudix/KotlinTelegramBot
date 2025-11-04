package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val TELEGRAM_BASE_URL = "https://api.telegram.org/bot"

data class Update(val chatId: Int, val messageText: String)

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0

    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updateId)
        println(updates)

        val startUpdateIdIndex = updates.lastIndexOf("update_id")
        val endUpdateIdIndex = updates.lastIndexOf(",\n\"message\"")
        if (startUpdateIdIndex == -1 || endUpdateIdIndex == -1) continue

        val updateInfo = parseHttpRequest(updates)

        val updateIdString = updates.substring(startUpdateIdIndex + 11, endUpdateIdIndex)
        updateId = updateIdString.toInt() + 1
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdates = "$TELEGRAM_BASE_URL$botToken/getUpdates?offset=$updateId"
    val urlGetMe = "$TELEGRAM_BASE_URL$botToken/getMe"

    val client: HttpClient = HttpClient.newBuilder().build()
    val requestGetUpdates: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val requestGetMe: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetMe)).build()
    val response: HttpResponse<String?> = client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString())

    return response.body().toString()
}

fun parseHttpRequest(update: String): Update {
    val startChatIdIndex = update.lastIndexOf("id") + 4
    val endChatIdIndex = update.lastIndexOf(",\"first_name\"")
    val chatIdString = update.substring(startChatIdIndex, endChatIdIndex).toInt()

    val startTextIndex = update.lastIndexOf("text") + 7
    val endTextIndex = update.lastIndexOf("\"}")
    val textString = update.substring(startTextIndex, endTextIndex).trim()
    return Update(chatIdString, textString)
}

