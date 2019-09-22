package me.ryansimon.yelpfusion.core.interactor


import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import me.ryansimon.yelpfusion.core.functional.Either
import me.ryansimon.yelpfusion.core.network.Failure
import kotlin.system.measureTimeMillis

/**
 * @author https://raw.githubusercontent.com/android10/Android-CleanArchitecture-Kotlin/d5b92eb3df6e007c14a3ec4f739f4b0b2e140738/app/src/main/kotlin/com/fernandocejas/sample/core/interactor/UseCase.kt
 *
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 * This abstraction represents an execution unit for different use cases (this means that any use
 * case in the application should implement this contract).
 *
 * By convention each [UseCase] implementation will execute its job in a background thread
 * (kotlin coroutine) and will post the result in the UI thread.
 */
abstract class UseCase<out Type, in Params> where Type : Any {

    abstract suspend fun run(params: Params): Either<Failure, Type>

    operator fun invoke(
            coroutineScope: CoroutineScope,
            params: Params,
            onResult: (Either<Failure, Type>) -> Unit = {}) {
        var measure: Long = 0
        flow {
            val requestTime = measureTimeMillis {
                emit(run(params))
            }

            measure += requestTime

            Log.d("Request time", "$requestTime ms")
        }
                .flowOn(Dispatchers.IO)
                .onStart {
                    val delayTime = measureTimeMillis {
                        delay(2000)
                    }

                    measure += delayTime

                    Log.d("Delay time", "$delayTime ms")
                }
                .onEach {
                    onResult(it)
                }
                .onCompletion { Log.d("Flow done in", "$measure ms") }
                .launchIn(coroutineScope)
    }

    class None
}