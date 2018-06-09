package me.ryansimon.yelpfusion.feature.business

import com.google.gson.annotations.SerializedName

/**
 * @author Ryan Simon
 */
data class Business(val name: String, @SerializedName("image_url") val imageUrl: String)