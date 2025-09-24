package org.example.task_2

import java.io.File

fun main() {

    val dictionary = mutableListOf<Word>()

    val wordFile = File("words.txt")

    val readFile = wordFile.readLines()

    for (line in readFile) {
        val split = line.split("|")

        val word = Word(split[0], split[1], split.getOrNull(2)?.toIntOrNull() ?: 0)
        dictionary.add(word)
    }

    dictionary.forEach { println(it) }
}

data class Word(
    val text: String,
    val translate: String,
    val correctAnswerCount: Int? = 0,
)