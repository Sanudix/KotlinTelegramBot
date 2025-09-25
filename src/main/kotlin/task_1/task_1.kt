package org.example

import java.io.File

fun main() {
    val file = File("words.txt")
    file.writeText("hello привет\ndog собака\ncat кошка")

    val strings = file.readLines()

    strings.forEach { println(it) }
}