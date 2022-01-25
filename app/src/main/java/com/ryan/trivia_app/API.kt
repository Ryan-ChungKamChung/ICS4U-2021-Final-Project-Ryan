package com.ryan.trivia_app

import android.app.Activity
import android.content.Intent
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
    fun request(url: String): String? = try { URL(url).readText() } catch (e: Exception) { null }

    /**
     * Parses categories API response.
     *
     * @param json as String
     * @return Categories
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
                    if (name.startsWith("Entertainment: ")) name.drop(15) else name
                )
            )
        }

        // Random numbers
        val randomList = (0 until allCategories.size).shuffled().take(4)
        for (randNum: Int in randomList) {
            while (true) {
                val categoryID = allCategories[randNum].id
                if (categoryIsValid(categoryID) != null && categoryIsValid(categoryID) == true) {
                    usedCategories.add(allCategories[randNum])
                    break
                } else {
                    categoryID + 1
                }
            }
        }

        return usedCategories
    }

    private fun categoryIsValid(id: Int): Boolean? {
        val json = request("https://opentdb.com/api_count.php?category=$id")
        return if (json != null) {
            JSONObject(json).getJSONObject("category_question_count")
                .getInt("total_question_count") >= 50
        } else {
            null
        }
    }

    fun parseQuestions(json: String): ArrayList<Question> {
        val questionsArray = ArrayList<Question>()
        val results = JSONObject(json).getJSONArray("results")
        for (iterator in 0 until results.length()) {
            // Get array of incorrect answers
            val incorrectAnswers = arrayOfNulls<String>(3)
            for (iterator2 in incorrectAnswers.indices) {
                incorrectAnswers[iterator2] = (results[iterator] as JSONObject)
                    .getJSONArray("incorrect_answers")
                    .getString(iterator2)
            }

            val jsonObject = results[iterator] as JSONObject
            questionsArray.add(
                Question(
                    htmlToString(jsonObject.getString("question")),
                    htmlToString(jsonObject.getString("correct_answer")),
                    htmlToString(incorrectAnswers[0]!!),
                    if (incorrectAnswers[1] != null) htmlToString(incorrectAnswers[1]!!)
                    else null,
                    if (incorrectAnswers[2] != null) htmlToString(incorrectAnswers[2]!!)
                    else null
                )
            )
        }
        return questionsArray
    }

    /**
     * Returns a reusable Intent for internet connection issues.
     *
     * @param activity that the fragment is in
     * @return intent
     */
    fun internetError(activity: Activity): Intent =
        Intent(activity, MainActivity::class.java).putExtra(
            "error",
            "Something went wrong. Please check your internet connection" +
                " and try again shortly."
        )

    /**
     * Converts HTML entities.
     *
     * @param text String to decode
     * @return decoded String with special characters
     */
    @Suppress("DEPRECATION")
    private fun htmlToString(text: String): String = if (Build.VERSION.SDK_INT >= 24) {
        Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString()
    } else {
        Html.fromHtml(text).toString()
    }
}
