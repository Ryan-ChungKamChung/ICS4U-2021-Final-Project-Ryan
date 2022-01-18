package com.ryan.trivia_app

data class TriviaData(
    val category: String,
    val question: String,
    val rightAnswer: String,
    val wrongAnswer1: String,
    val wrongAnswer2: String?,
    val wrongAnswer3: String?
)
