package me.ryansimon.yelpfusion.core.interactor


import kotlinx.coroutines.*
import me.ryansimon.yelpfusion.core.functional.Either
import me.ryansimon.yelpfusion.core.network.Failure

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
        coroutineScope.launch {
            onResult(withContext(Dispatchers.IO) {
                run(params)
            })
        }
    }

    class None
}