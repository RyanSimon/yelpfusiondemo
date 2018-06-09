package me.ryansimon.yelpfusion.network

import android.content.Context
import me.ryansimon.yelpfusion.extension.networkInfo

/**
 * @author Ryan Simon
 */

class InternetConnectionHandler(private val context: Context) {
    val isConnected
        get() = context.networkInfo.isConnectedOrConnecting
}