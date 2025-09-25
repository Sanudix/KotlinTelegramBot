package org.example.task_3

import java.io.File

fun main() {

    val dictionary = loadDictionary()

    dictionary.forEach { println(it) }

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
            "2" -> println("Выбран пункт 'Статистика'\n")
            "0" -> return
            else -> println("Введите число 1, 2 или 0.\n")
        }
    }
}

fun loadDictionary(): MutableList<Word> {

    val dictionary = mutableListOf<Word>()

    val wordFile = File("words.txt")

    val readFile = wordFile.readLines()

    for (line in readFile) {
        val split = line.split("|")

        val word = Word(split[0], split[1], split[2].toInt())
        dictionary.add(word)
    }

    return dictionary
}

data class Word(
    val text: String,
    val translate: String,
    val correctAnswerCount: Int = 0,
)