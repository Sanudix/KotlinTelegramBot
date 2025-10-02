package org.example.task_4

import java.io.File
import java.io.FileNotFoundException

fun main() {

    val dictionary = loadDictionary()

    while (true) {
        println(
            """
            Меню:
            1 - Учить слова
            2 - Статистика
            0 - Выход
        """.trimIndent()
        )

        print("Ввод: ")
        val userInput = readln()

        when (userInput) {
            "1" -> println("Выбран пункт 'Учить слова'\n")
            "2" -> printStatistics(dictionary)
            "0" -> return
            else -> println("Введите число 1, 2 или 0.\n")
        }
    }
}

data class Word(
    val text: String,
    val translate: String,
    val correctAnswerCount: Int? = 0,
)

fun loadDictionary(): MutableList<Word> {

    val dictionary = mutableListOf<Word>()

    try {
        val wordFile = File("words.txt")

        val readFile = wordFile.readLines()

        for (line in readFile) {
            val split = line.split("|")

            if (split.size == 3) {
                val word = Word(split[0], split[1], split[2].toIntOrNull())
                dictionary.add(word)
            } else if (split.size < 3) {
                println("Недостаточно данных для добавления слова.")
            } else {
                println("Неверный формат строки: IndexOutOfBoundsException.")
            }
        }
    } catch (e: FileNotFoundException) {
        println("Ошибка: Файл 'words.txt' не найден!")
    }

    return dictionary
}

fun printStatistics(wordsList: List<Word>) {

    val totalCount: Int = wordsList.count()
    val learnedCount: Int = wordsList.count { (it.correctAnswerCount ?: 0) >= 3 }

    var percent: Int

    if (totalCount > 0) {
        percent = ((learnedCount.toFloat() / totalCount.toFloat()) * 100).toInt()
    } else {
        percent = 0
    }

    println("\nВыучено $learnedCount из $totalCount слов | $percent%\n")
}