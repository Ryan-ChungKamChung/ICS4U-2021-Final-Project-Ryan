/*
 * Copyright 2022 Ryan Chung Kam Chung
 *
 * Category Request class.
 */

package com.ryan.trivia_app.model

import android.os.AsyncTask
import java.net.URL
import org.json.JSONObject

/**
 * Class that returns a parsed API response.
 *
 * @property url
 */
class CategoryAPIRequest(private val url: String) :
    AsyncTask<String, Void, ArrayList<Category>?>() {

    /**
     * Get request to a URL.
     *
     * @return JSON as String or null
     */
    override fun doInBackground(vararg p0: String): ArrayList<Category>? =
        try {
            parseCategories(URL(url).readText())
        } catch (e: Exception) {
            null
        }

    override fun onPostExecute(result: ArrayList<Category>?) {}

    /**
     * Parses categories API response.
     *
     * @param json as String.
     * @return Categories.
     */
    private fun parseCategories(json: String): ArrayList<Category> {
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
        val json = API().request("https://opentdb.com/api_count.php?category=$id")
        // Need 50 questions for a full game
        return if (json != null) {
            JSONObject(json).getJSONObject("category_question_count")
                .getInt("total_question_count") >= 50
        } else {
            null
        }
    }
}