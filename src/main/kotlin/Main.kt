package org.example

const val RIGHT_ANSWER_NUMBER = 3

data class Word(
    val text: String,
    val translate: String,
    var correctAnswerCount: Int = 0,
)

fun Question.asConsoleString(): String {
    val variants = this.variants
        .mapIndexed { index, word ->
            " ${index + 1} - ${word.translate}"
        }
        .joinToString(separator = "\n")
    return "\n" + this.correctWord.text + ":\n" + variants + "\n-----------\n 0 - Меню"
}

fun main() {

    val trainer = LearnWordsTrainer()

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
        when (readln()) {
            "1" -> learnWords(trainer)
            "2" -> {
                val statistics = trainer.getStatistics()
                println("\nВыучено ${statistics.learnedCount} из ${statistics.totalCount} слов | ${statistics.percent}%\n")
            }

            "0" -> return
            else -> println("Введите число 1, 2 или 0.\n")
        }
    }
}


fun learnWords(trainer: LearnWordsTrainer) {

    while (true) {
        val question = trainer.getNextQuestion()
        if (question == null) {
            println("Все слова выучены")
            break
        } else {
            println(question.asConsoleString())

            print("\nВведите число: ")
            val userAnswerInput = readln().toIntOrNull()
            if (userAnswerInput == 0) break

            if (trainer.checkAnswer(userAnswerInput?.minus(1))) {
                println("\nПравильно!")
            } else {
                println("\nНеправильно! ${question.correctWord.text} - это ${question.correctWord.translate}")
            }
        }
    }
}