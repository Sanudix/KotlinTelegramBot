package org.example

import java.io.File
import java.io.FileNotFoundException
import kotlin.ranges.contains

data class Statistics(
    val totalCount: Int,
    val learnedCount: Int,
    val percent: Int,
)

data class Question(
    val variants: List<Word>,
    val correctWord: Word,
)

class LearnWordsTrainer(private val countWordsQuestion: Int = 4, private val learnedAnswerCount: Int = 3, private val correctNumberOfElements: Int = 3) {

    private var question: Question? = null
    val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val totalCount: Int = dictionary.count()
        val learnedCount: Int = dictionary.count { it.correctAnswerCount >= learnedAnswerCount }
        val percent = if (totalCount > 0) {
            learnedCount * 100 / totalCount
        } else 0
        return Statistics(totalCount, learnedCount, percent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswerCount < learnedAnswerCount }
        if (notLearnedList.isEmpty()) return null
        val questionWords = if (notLearnedList.size < countWordsQuestion) {
            val learnedList = dictionary.filter { it.correctAnswerCount >= learnedAnswerCount }.shuffled()
            notLearnedList.shuffled().take(countWordsQuestion) + learnedList.take(countWordsQuestion - notLearnedList.size)
        } else {
            notLearnedList.shuffled().take(countWordsQuestion)
        }.shuffled()

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
                in it.variants.indices -> {
                    if (userAnswerInput == correctAnswerId) {
                        it.correctWord.correctAnswerCount++
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

                if (split.size == correctNumberOfElements) {
                    val word = Word(split[0], split[1], (split[2].toIntOrNull() ?: 0))
                    dictionary.add(word)
                } else if (split.size < learnedAnswerCount) {
                    println("Недостаточно данных для добавления слова.")
                }
            }
        } catch (e: FileNotFoundException) {
            println("Ошибка: Файл 'words.txt' не найден!")
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalArgumentException("Некорректный файл")
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
