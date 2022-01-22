package com.ryan.trivia_app

import android.app.Activity
import android.content.Intent
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
    fun request(url: String): String? {
        return try {
            URL(url).readText()
        } catch (e: Exception) {
            null
        }
    }

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
            usedCategories.add(allCategories[randNum])
        }

        return usedCategories
    }

    /**
     * Returns a reusable Intent for internet connection issues.
     *
     * @param activity that the fragment is in
     * @return intent
     */
    fun internetError(activity: Activity): Intent {
        return Intent(activity, MainActivity::class.java).putExtra(
            "error",
            "Something went wrong. Please check your internet connection" +
                " and try again shortly."
        )
    }
}
