package org.example

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
    var correctAnswerCount: Int? = 0,
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

fun learnWords(wordsList: MutableList<Word>) {

    while (true) {

        val notLearnedList = mutableListOf<Word>()
        var counter = 0

        val shuffledWordsList = wordsList.shuffled()

        shuffledWordsList.forEach { element ->
            if ((element.correctAnswerCount ?: 0) < 3) {
                notLearnedList.add(element)
                counter++
            }
        }

        if (counter == 0) {
            println("\nВсе слова выучены.")
            break
        } else {
            var questionWords = notLearnedList.take(4)

            val currentEnglishWord = questionWords[0].text
            val currentTranslateWord = questionWords[0].translate
            var currentCorrectAnswerCount = questionWords[0].correctAnswerCount

            questionWords = questionWords.shuffled()

            println("\n${currentEnglishWord}:")


            questionWords.forEachIndexed { index, word ->
                println(" ${index + 1} - ${word.translate}")
            }
            println(" 0 - главное меню")

            print("\nВведите ответ: ")
            var userAnswer = readln().toIntOrNull()

            while (userAnswer == null) {
                print("\nНекорректный ввод! Введите число из списка: ")
                userAnswer = readln().toIntOrNull()
            }

            if (userAnswer == 0) {
                break
            }
            if (userAnswer - 1 < questionWords.size) {
                if (questionWords[userAnswer - 1].translate == currentTranslateWord) {
                    currentCorrectAnswerCount = currentCorrectAnswerCount!! + 1
                    println("\nПравильный ответ!")

                    val index =
                        wordsList.indexOfFirst { it.text == currentEnglishWord && it.translate == currentTranslateWord }

                    wordsList[index].correctAnswerCount = currentCorrectAnswerCount
                } else {
                    println("\nНеправильный ответ!")
                }
            } else {
                println("\nВведено неверное число!")
            }

        }
    }
}