package org.example

import java.io.File
import java.io.FileNotFoundException
import kotlin.ranges.contains

const val COUNT_OF_WORDS_IN_QUESTIONS = 4

data class Statistics(
    val totalCount: Int,
    val learnedCount: Int,
    val percent: Int,
)

data class Question(
    val variants: List<Word>,
    val correctWord: Word,
)

class LearnWordsTrainer {

    private var question: Question? = null
    val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val totalCount: Int = dictionary.count()
        val learnedCount: Int = dictionary.count { it.correctAnswerCount >= RIGHT_ANSWER_NUMBER }
        val percent = learnedCount * 100 / totalCount
        return Statistics(totalCount, learnedCount, percent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswerCount < RIGHT_ANSWER_NUMBER }
        if (notLearnedList.isEmpty()) return null
        val questionWords = notLearnedList.shuffled().take(COUNT_OF_WORDS_IN_QUESTIONS)
        val correctAnswer = questionWords.random()
        question = Question(
            variants = questionWords,
            correctAnswer,
        )
        return question
    }

    fun checkAnswer(userAnswerInput: Int?): Boolean {
        return question?.let {
            val correctAnswerId = it.variants.indexOf(it.correctWord)
            when (userAnswerInput) {
                in question?.variants?.indices ?: return false -> {
                    if (userAnswerInput == correctAnswerId) {
                        question?.correctWord?.correctAnswerCount++
                        saveDictionary(dictionary)
                        true
                    } else {
                        false
                    }
                }

                else -> false
            }
        } ?: false
    }

    private fun loadDictionary(): MutableList<Word> {

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

    private fun saveDictionary(wordsList: List<Word>, filename: String = "words.txt") {
        val content = wordsList.joinToString("\n") { word ->
            "${word.text}|${word.translate}|${word.correctAnswerCount}"
        }
        File(filename).writeText(content)
    }
}
