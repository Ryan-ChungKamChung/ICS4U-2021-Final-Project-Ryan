/*
 * Copyright 2022 Ryan Chung Kam Chung
 *
 * Category Request class.
 */

package com.ryan.trivia_app.model

import android.os.AsyncTask
import android.os.Build
import android.text.Html
import java.net.URL
import org.json.JSONObject

/**
 * Class that returns a parsed API response.
 *
 * @property url
 */
class TriviaAPIRequest(private val url: String) : AsyncTask<String, Void, ArrayList<Question>?>() {

    /**
     * Get request to a URL.
     *
     * @return JSON as String or null
     */
    override fun doInBackground(vararg p0: String): ArrayList<Question>? =
        try {
            parseQuestions(URL(url).readText())
        } catch (e: Exception) {
            null
        }

    override fun onPostExecute(result: ArrayList<Question>?) {}

    /**
     * Parses the question API call response.
     *
     * @param json the returned JSON.
     * @return a parsed ArrayList of all questions.
     */
    private fun parseQuestions(json: String): ArrayList<Question> {
        // Questions to be sent back
        val questionsArray = ArrayList<Question>()
        // Entry point of the JSON data
        val results = JSONObject(json).getJSONArray("results")
        // For every JSONObject inside results
        for (iterator in 0 until results.length()) {
            // Get array of incorrect answers
            val incorrectAnswers = arrayOfNulls<String>(3)
            for (iterator2 in incorrectAnswers.indices) {
                incorrectAnswers[iterator2] = (results[iterator] as JSONObject)
                    .getJSONArray("incorrect_answers")
                    .getString(iterator2)
            }
            // Adds all relevant information to questionsArray
            val jsonObject = results[iterator] as JSONObject
            questionsArray.add(
                Question(
                    htmlToString(jsonObject.getString("question")),
                    htmlToString(jsonObject.getString("correct_answer")),
                    htmlToString(incorrectAnswers[0]!!),
                    htmlToString(incorrectAnswers[1]!!),
                    htmlToString(incorrectAnswers[2]!!),
                )
            )
        }
        return questionsArray
    }

    /**
     * Converts HTML entities.
     *
     * @param text String to decode.
     * @return decoded String with special characters.
     */
    @Suppress("DEPRECATION")
    private fun htmlToString(text: String): String = if (Build.VERSION.SDK_INT >= 24) {
        Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString()
    } else {
        Html.fromHtml(text).toString()
    }
}