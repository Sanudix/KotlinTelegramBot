package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.Date
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TelegramResponse(
    @Json(name = "ok") val ok: Boolean,
    @Json(name = "result") val result: List<Update>
)

@JsonClass(generateAdapter = true)
data class Update(
    @Json(name = "update_id") val updateId: Long,
    @Json(name = "message") val message: Message? = null,
    @Json(name = "edited_message") val editedMessage: Message? = null
)

@JsonClass(generateAdapter = true)
data class Message(
    @Json(name = "message_id") val messageId: Long,
    @Json(name = "from") val from: User,
    @Json(name = "chat") val chat: Chat,
    @Json(name = "date") val date: Long,
    @Json(name = "text") val text: String? = null,
    @Json(name = "entities") val entities: List<MessageEntity>? = null
)

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "id") val id: Long,
    @Json(name = "is_bot") val isBot: Boolean,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "username") val username: String? = null,
    @Json(name = "language_code") val languageCode: String? = null
)

@JsonClass(generateAdapter = true)
data class Chat(
    @Json(name = "id") val id: Long,
    @Json(name = "first_name") val firstName: String? = null,
    @Json(name = "username") val username: String? = null,
    @Json(name = "type") val type: String,
    @Json(name = "title") val title: String? = null
)

@JsonClass(generateAdapter = true)
data class MessageEntity(
    @Json(name = "offset") val offset: Int,
    @Json(name = "length") val length: Int,
    @Json(name = "type") val type: String
)

fun main(args: Array<String>) {

    val botToken = args[0]
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates"

    val client: HttpClient = HttpClient.newBuilder().build()

    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()

    val response: HttpResponse<String?> = client.send(request, HttpResponse.BodyHandlers.ofString())

    processResponseWithMoshi(response.body())
}

fun processResponseWithMoshi(jsonResponse: String?) {
    if (jsonResponse.isNullOrEmpty()) {
        println("Получен пустой ответ")
        return
    }

    try {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val adapter = moshi.adapter(TelegramResponse::class.java)

        val telegramResponse = adapter.fromJson(jsonResponse)

        if (telegramResponse != null) {
            printTelegramResponse(telegramResponse)
        } else {
            println("Ошибка: не удалось десериализовать JSON")
        }

    } catch (e: Exception) {
        println("Ошибка при десериализации JSON: ${e.message}")
        e.printStackTrace()
    }
}

fun printTelegramResponse(response: TelegramResponse) {
    println("ДЕСЕРИАЛИЗОВАННЫЕ ДАННЫЕ TELEGRAM")
    println("Статус ответа: ${if (response.ok) "УСПЕХ" else "ОШИБКА"}")
    println("Количество обновлений: ${response.result.size}")
    println()

    if (response.result.isNotEmpty()) {
        response.result.forEachIndexed { index, update ->
            println("--- Обновление ${index + 1} ---")
            println("Update ID: ${update.updateId}")

            val message = update.message ?: update.editedMessage
            if (message != null) {
                val messageType = if (update.editedMessage != null) "Редактированное сообщение" else "Сообщение"
                printMessageInfo(message, messageType)
            } else {
                println("Тип обновления не поддерживается")
            }
            println()
        }
    } else {
        println("Нет новых сообщений")
    }
}

fun printMessageInfo(message: Message, type: String) {
    println("$type:")
    println("   Message ID: ${message.messageId}")
    println("   Дата: ${Date(message.date * 1000)}")

    message.text?.let { text ->
        println("   Текст: $text")
    }

    println("   Отправитель:")
    println("      - ID: ${message.from.id}")
    println("      - Имя: ${message.from.firstName}")
    println("      - Бот: ${if (message.from.isBot) "Да" else "Нет"}")
    message.from.username?.let { username ->
        println("      - Username: @$username")
    }
    message.from.languageCode?.let { lang ->
        println("      - Язык: $lang")
    }

    println("   Чат:")
    println("      - ID: ${message.chat.id}")
    println("      - Тип: ${message.chat.type}")
    message.chat.firstName?.let { firstName ->
        println("      - Имя: $firstName")
    }
    message.chat.username?.let { username ->
        println("      - Username: @$username")
    }
    message.chat.title?.let { title ->
        println("      - Название: $title")
    }

    message.entities?.let { entities ->
        println("   Сущности сообщения:")
        entities.forEach { entity ->
            println("      - ${entity.type}: позиция ${entity.offset}, длина ${entity.length}")
            message.text?.let { text ->
                val entityText = text.substring(entity.offset, entity.offset + entity.length)
                println("        Текст: '$entityText'")
            }
        }
    }
}