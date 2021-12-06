package com.kumjinwoo.smartfarm

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlin.collections.HashMap

@IgnoreExtraProperties
data class Post(
    var user: String? = "",
    var startDate: String? = "",
    var plantName: String? = "",
    var type: String? = "",
    var division: String? = "",
    var stars: MutableMap<String, Boolean> = HashMap()
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "user" to user,
            "startDate" to startDate,
            "plantName" to plantName,
            "type" to type,
            "division" to division,
            "stars" to stars
        )
    }
}