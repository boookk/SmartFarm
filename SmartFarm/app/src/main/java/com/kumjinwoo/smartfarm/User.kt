package com.kumjinwoo.smartfarm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    val user: String,
    val pw: String
) : Parcelable