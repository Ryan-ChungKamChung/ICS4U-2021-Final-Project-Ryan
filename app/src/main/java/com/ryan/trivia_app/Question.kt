package com.ryan.trivia_app

data class Question(
    val question: String,
    val rightAnswer: String,
    val wrongAnswer1: String,
    val wrongAnswer2: String?,
    val wrongAnswer3: String?
)
