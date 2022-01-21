package com.ryan.trivia_app

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.net.URL
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class API {
    /**
     * Getting trivia API and returning a parsed version.
     *
     * @param context activity/fragment this is being run in.
     * @param category category of questions.
     * @param difficulty difficulty of the questions.
     * @return parsed questions and answers.
     */
    fun callTriviaAPI(
        context: Context,
        category: String,
        difficulty: String? = null
    ): ArrayList<Question> {
        // URL to get information from
        val url = "https://opentdb.com/api.php?amount=50"
        // Parsed ArrayList of questions and answers
        val result = ArrayList<Question>()
        // Uses a background thread
        thread {
            // Creates request
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null, { response ->
                    // JSONArray inside of a JSONObject
                    val jsonArray = response.getJSONArray("results")
                    // For every question inside of jsonArray
                    for (iterator in 0 until jsonArray.length()) {
                        // Singular JSONObject
                        val jsonObject = jsonArray[iterator] as JSONObject
                        // Whether the question is multiple choice or not
                        val multipleChoice = jsonObject.getString("type") == "multiple"
                        /* Parses the JSONObject and adds the data to a TriviaData object
                           Adds the TriviaData object to the array to be sent to the View. */
                        result.add(
                            Question(
                                jsonObject.getString("category"),
                                jsonObject.getString("question"),
                                jsonObject.getString("correct_answer"),
                                jsonObject.getString("incorrect_answers"),
                                if (multipleChoice) jsonObject.getString("incorrect_answers")
                                else null,
                                if (multipleChoice) jsonObject.getString("incorrect_answers")
                                else null
                            )
                        )
                    }
                }, {}
            )
            // Add request to a queue
            Volley.newRequestQueue(context).add(jsonObjectRequest)
        }
        return result
    }

    fun request(url: String): String? {
        return try {
            URL(url).readText()
        } catch (e: Exception) {
            null
        }
    }
}
