package me.ryansimon.yelpfusion.core.network

import android.content.Context
import me.ryansimon.yelpfusion.core.extension.networkInfo

/**
 * @author Ryan Simon
 */

class InternetConnectionHandler(private val context: Context) {
    val isConnected
        get() = context.networkInfo.isConnectedOrConnecting
}