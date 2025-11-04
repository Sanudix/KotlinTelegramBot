package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val TELEGRAM_BASE_URL = "https://api.telegram.org/bot"

data class Update(
    val chatId: Int,
    val messageText: String,
    val messageId: Int,
    val userId: Int,
    val firstName: String,
    val username: String,
    val isBot: Boolean,
    val languageCode: String,
    val chatType: String,
    val date: Int,
)

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

    val startMessageIdIndex = update.lastIndexOf("message_id") + 12
    val endMessageIdIndex = update.indexOf(",", startMessageIdIndex)
    val messageIdString = update.substring(startMessageIdIndex, endMessageIdIndex).toInt()

    val startUserIdIndex = update.lastIndexOf("\"from\":{\"id\":") + 13
    val endUserIdIndex = update.indexOf(",", startUserIdIndex)
    val userIdString = update.substring(startUserIdIndex, endUserIdIndex).toInt()

    val startFirstNameIndex = update.lastIndexOf("first_name") + 13
    val endFirstNameIndex = update.indexOf("\",", startFirstNameIndex)
    val firstNameString = update.substring(startFirstNameIndex, endFirstNameIndex).trim()

    val startUsernameIndex = update.lastIndexOf("username") + 11
    val endUsernameIndex = update.indexOf("\",", startUsernameIndex)
    val usernameString = update.substring(startUsernameIndex, endUsernameIndex).trim()

    val startIsBotIndex = update.lastIndexOf("is_bot") + 8
    val endIsBotIndex = update.indexOf(",", startIsBotIndex)
    val isBotString = update.substring(startIsBotIndex, endIsBotIndex).trim().toBoolean()

    val startLangIndex = update.lastIndexOf("language_code") + 16
    val endLangIndex = update.indexOf("\"", startLangIndex)
    val languageCodeString = update.substring(startLangIndex, endLangIndex).trim()

    val startChatTypeIndex = update.lastIndexOf("type") + 7
    val endChatTypeIndex = update.indexOf("\"", startChatTypeIndex)
    val chatTypeString = update.substring(startChatTypeIndex, endChatTypeIndex).trim()

    val startDateIndex = update.lastIndexOf("date") + 6
    val endDateIndex = update.indexOf(",", startDateIndex)
    val dateString = update.substring(startDateIndex, endDateIndex).toInt()

    return Update(
        chatId = chatIdString,
        messageText = textString,
        messageId = messageIdString,
        userId = userIdString,
        firstName = firstNameString,
        username = usernameString,
        isBot = isBotString,
        languageCode = languageCodeString,
        chatType = chatTypeString,
        date = dateString
    )
}

