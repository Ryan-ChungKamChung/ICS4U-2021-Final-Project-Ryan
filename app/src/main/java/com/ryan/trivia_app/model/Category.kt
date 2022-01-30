package com.ryan.trivia_app.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Parsed data of all relevant info of a question.
 *
 * @property id the category ID.
 * @property name the category name.
 */
@Parcelize
data class Category(val id: Int, val name: String) : Parcelable
