package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val TELEGRAM_BASE_URL = "https://api.telegram.org/bot"

data class Update(
    val chatId: Int?,
    val messageText: String?,
    val messageId: Int?,
    val userId: Int?,
    val firstName: String?,
    val username: String?,
    val isBot: Boolean?,
    val languageCode: String?,
    val chatType: String?,
    val date: Int?,
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

        val updateInfo = parseRegex(updates)

        val updateIdString = updates.substring(startUpdateIdIndex + 11, endUpdateIdIndex)
        updateId = updateIdString.toInt() + 1
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdates = "$TELEGRAM_BASE_URL$botToken/getUpdates?offset=$updateId"

    val client: HttpClient = HttpClient.newBuilder().build()
    val requestGetUpdates: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val response: HttpResponse<String?> = client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString())

    return response.body().toString()
}

fun parseRegex(update: String): Update {

    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
    val charIdMatchResult: MatchResult? = chatIdRegex.find(update)
    val chatIdGroups = charIdMatchResult?.groups
    val chatId = chatIdGroups?.get(1)?.value?.toInt()

    val textRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val textMatchResult: MatchResult? = textRegex.find(update)
    val textGroups = textMatchResult?.groups
    val text = textGroups?.get(1)?.value

    val messageIdRegex: Regex = "\"message_id\":(\\d+)".toRegex()
    val messageIdMatchResult: MatchResult? = messageIdRegex.find(update)
    val messageIdGroups = messageIdMatchResult?.groups
    val messageId = messageIdGroups?.get(1)?.value?.toInt()

    val userIdRegex: Regex = "\"from\":\\{\"id\":(\\d+)".toRegex()
    val userIdMatchResult: MatchResult? = userIdRegex.find(update)
    val userIdGroups = userIdMatchResult?.groups
    val userId = userIdGroups?.get(1)?.value?.toInt()

    val firstNameRegex: Regex = "\"first_name\":\"(.+?)\"".toRegex()
    val firstNameMatchResult: MatchResult? = firstNameRegex.find(update)
    val firstNameGroups = firstNameMatchResult?.groups
    val firstName = firstNameGroups?.get(1)?.value

    val usernameRegex: Regex = "\"username\":\"(.+?)\"".toRegex()
    val usernameMatchResult: MatchResult? = usernameRegex.find(update)
    val usernameGroups = usernameMatchResult?.groups
    val username = usernameGroups?.get(1)?.value

    val isBotRegex: Regex = "\"is_bot\":(true|false)".toRegex()
    val isBotMatchResult: MatchResult? = isBotRegex.find(update)
    val isBotGroups = isBotMatchResult?.groups
    val isBot = isBotGroups?.get(1)?.value?.toBoolean()

    val languageCodeRegex: Regex = "\"language_code\":\"(.+?)\"".toRegex()
    val languageCodeMatchResult: MatchResult? = languageCodeRegex.find(update)
    val languageCodeGroups = languageCodeMatchResult?.groups
    val languageCode = languageCodeGroups?.get(1)?.value

    val chatTypeRegex: Regex = "\"type\":\"(.+?)\"".toRegex()
    val chatTypeMatchResult: MatchResult? = chatTypeRegex.find(update)
    val chatTypeGroups = chatTypeMatchResult?.groups
    val chatType = chatTypeGroups?.get(1)?.value

    val dateRegex: Regex = "\"date\":(\\d+)".toRegex()
    val dateMatchResult: MatchResult? = dateRegex.find(update)
    val dateGroups = dateMatchResult?.groups
    val date = dateGroups?.get(1)?.value?.toInt()

    return Update(
        chatId = chatId,
        messageText = text,
        messageId = messageId,
        userId = userId,
        firstName = firstName,
        username = username,
        isBot = isBot,
        languageCode = languageCode,
        chatType = chatType,
        date = date
    )
}

