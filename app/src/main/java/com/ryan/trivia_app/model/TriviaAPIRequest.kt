/*
 * Copyright 2022 Ryan Chung Kam Chung
 *
 * Category Request class.
 */

package com.ryan.trivia_app.model

import android.os.AsyncTask
import java.net.URL

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
        try { API().parseQuestions(URL(url).readText()) } catch (e: Exception) { null }

    override fun onPostExecute(result: ArrayList<Question>?) {}
}