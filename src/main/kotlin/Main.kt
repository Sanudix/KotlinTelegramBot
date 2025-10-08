package org.example

import java.io.File
import java.io.FileNotFoundException

const val RIGHT_ANSWER_NUMBER = 3
const val COUNT_OF_WORDS_IN_QUESTIONS = 4

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
            "1" -> learnWords(dictionary)
            "2" -> printStatistics(dictionary)
            "0" -> return
            else -> println("Введите число 1, 2 или 0.\n")
        }
    }
}

data class Word(
    val text: String,
    val translate: String,
    var correctAnswerCount: Int = 0,
)

fun loadDictionary(): MutableList<Word> {

    val dictionary = mutableListOf<Word>()

    try {
        val wordFile = File("words.txt")

        val readFile = wordFile.readLines()

        for (line in readFile) {
            val split = line.split("|")

            if (split.size == RIGHT_ANSWER_NUMBER) {
                val word = Word(split[0], split[1], (split[2].toIntOrNull() ?: 0))
                dictionary.add(word)
            } else if (split.size < RIGHT_ANSWER_NUMBER) {
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

fun saveDictionary(wordsList: List<Word>, filename: String = "words.txt") {
    val content = wordsList.joinToString("\n") { word ->
        "${word.text}|${word.translate}|${word.correctAnswerCount}"
    }
    File(filename).writeText(content)
}

fun printStatistics(wordsList: List<Word>) {

    val totalCount: Int = wordsList.count()
    val learnedCount: Int = wordsList.count { it.correctAnswerCount >= RIGHT_ANSWER_NUMBER }

    var percent: Int

    if (totalCount > 0) {
        percent = ((learnedCount.toFloat() / totalCount.toFloat()) * 100).toInt()
    } else {
        percent = 0
    }

    println("\nВыучено $learnedCount из $totalCount слов | $percent%\n")
}

fun learnWords(wordsList: MutableList<Word>) {

    while (true) {

        val notLearnedList = mutableListOf<Word>()
        var counter = 0

        wordsList.forEach { element ->
            if (element.correctAnswerCount  < RIGHT_ANSWER_NUMBER) {
                notLearnedList.add(element)
                counter++
            }
        }

        if (counter == 0) {
            println("\nВсе слова выучены.")
            saveDictionary(wordsList)
            break
        } else {

            val questionWords = notLearnedList.shuffled().take(COUNT_OF_WORDS_IN_QUESTIONS)

            val questionWordsCount = questionWords.size

            val correctAnswerId = (0 until questionWordsCount).random()

            var currentCorrectAnswerCount = questionWords[correctAnswerId].correctAnswerCount

            println("\n${questionWords[correctAnswerId].text}:")


            questionWords.forEachIndexed { index, word ->
                println(" ${index + 1} - ${word.translate}")
            }
            println("-----------\n 0 - Меню")

            print("\nВведите ответ: ")
            var userAnswerInput = readln().toIntOrNull()

            while (userAnswerInput == null) {
                print("\nНекорректный ввод! Введите число из списка: ")
                userAnswerInput = readln().toIntOrNull()
            }

            if (userAnswerInput == 0) {
                saveDictionary(wordsList)
                break
            }
            if (userAnswerInput - 1 < questionWords.size) {
                if (questionWords[userAnswerInput - 1].translate == questionWords[correctAnswerId].translate) {
                    currentCorrectAnswerCount = currentCorrectAnswerCount + 1
                    println("\nПравильно!")

                    val index =
                        wordsList.indexOfFirst { it.text == questionWords[correctAnswerId].text && it.translate == questionWords[correctAnswerId].translate }

                    wordsList[index].correctAnswerCount = currentCorrectAnswerCount
                } else {
                    println("\nНеправильно! ${questionWords[correctAnswerId].text} - это ${questionWords[correctAnswerId].translate}")
                }
            } else {
                println("\nВведено неверное число!")
            }

        }
    }
}