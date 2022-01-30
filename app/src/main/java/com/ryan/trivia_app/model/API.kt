/*
 * Copyright 2022 Ryan Chung Kam Chung
 *
 * API class that contains functions related to API requests and parsing.
 */

package com.ryan.trivia_app.model

import android.os.Build
import android.text.Html
import java.net.URL
import org.json.JSONObject

/** API-related class. */
class API {
    /**
     * Get request to a URL.
     *
     * @param url to make a GET request
     * @return JSON as String or null
     */
    fun request(url: String): String? = try {
        URL(url).readText()
    } catch (e: Exception) {
        null
    }

    /**
     * Parses categories API response.
     *
     * @param json as String.
     * @return Categories.
     */
    fun parseCategories(json: String): ArrayList<Category> {
        // All categories from API
        val allCategories = ArrayList<Category>()
        // Used categories from API
        val usedCategories = ArrayList<Category>()
        // JSONArray of categories
        val jsonArray = JSONObject(json).getJSONArray("trivia_categories")
        // Parse loop of JSONObjects inside of JSONArray
        for (iterator in 0 until jsonArray.length()) {
            // Name of category
            val name = (jsonArray[iterator] as JSONObject).getString("name")
            // Adds id and name
            allCategories.add(
                Category(
                    (jsonArray[iterator] as JSONObject).getInt("id"),
                    // Filters unnecessary substrings
                    when {
                        name.startsWith("Entertainment: Japanese ") -> name.drop(24)
                        name.startsWith("Entertainment: ") -> name.drop(15)
                        else -> name
                    }
                )
            )
        }

        // Random unique numbers
        val randomList = (0 until allCategories.size).shuffled().take(4)
        for (randNum: Int in randomList) {
            while (true) {
                val categoryID = allCategories[randNum].id
                // If it's a valid category
                if (categoryIsValid(categoryID) == true) {
                    // Add it to the usedCategories array to be sent back
                    usedCategories.add(allCategories[randNum])
                    break
                } else {
                    // Test the next category if it isn't valid
                    categoryID + 1
                }
            }
        }

        return usedCategories
    }

    /**
     * Checks if a category has enough questions (50) for a full game
     *
     * @param id the category ID
     * @return returns if it's valid, or null if there is an internet error
     */
    private fun categoryIsValid(id: Int): Boolean? {
        val json = request("https://opentdb.com/api_count.php?category=$id")
        // Need 50 questions for a full game
        return if (json != null) {
            JSONObject(json).getJSONObject("category_question_count")
                .getInt("total_question_count") >= 50
        } else {
            null
        }
    }

    /**
     * Parses the question API call response.
     *
     * @param json the returned JSON.
     * @return a parsed ArrayList of all questions.
     */
    fun parseQuestions(json: String): ArrayList<Question> {
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
