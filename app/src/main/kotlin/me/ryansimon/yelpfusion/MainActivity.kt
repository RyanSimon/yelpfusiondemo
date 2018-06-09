package me.ryansimon.yelpfusion

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.ryansimon.yelpfusion.feature.business.BusinessesApi
import me.ryansimon.yelpfusion.feature.business.BusinessesRepository
import me.ryansimon.yelpfusion.network.ApiConfiguration
import me.ryansimon.yelpfusion.network.InternetConnectionHandler

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val apiConfiguration = ApiConfiguration(BuildConfig.YELP_API_KEY)
        val businessesRepository = BusinessesRepository(
                apiConfiguration.retrofit.create(BusinessesApi::class.java),
                InternetConnectionHandler(this)
        )
        val callable = { businessesRepository.search("pizza", "Irvine, CA") }
        val response = Single.fromCallable(callable)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSuccess({ success -> success.either({}, { businessesResponse -> Log.d("Success", "Yay") })})
                        .doOnError({ error -> Log.d("Error", error.message)})
                        .subscribe()
    }
}
