package me.ryansimon.yelpfusion.core.interactor

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import me.ryansimon.yelpfusion.core.functional.Either
import me.ryansimon.yelpfusion.core.network.Failure
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test

/**
 * @author https://raw.githubusercontent.com/android10/Android-CleanArchitecture-Kotlin/d5b92eb3df6e007c14a3ec4f739f4b0b2e140738/app/src/test/kotlin/com/fernandocejas/sample/core/interactor/UseCaseTest.kt
 */
class UseCaseTest {

    private val typeTest = "Test"
    private val typeParam = "ParamTest"

    private lateinit var useCase: UseCase<MyType, MyParams>

    @Before
    fun setUp() {
        useCase = mock { onBlocking { run(any()) } doReturn Either.Success(MyType(typeTest)) }
    }

    @Test
    fun `Should return 'Either' of use case type`() {
        val params = MyParams(typeParam)
        val result = runBlocking { useCase.run(params) }

        result shouldEqual Either.Success(MyType(typeTest))
    }

    @Test
    fun `Should return correct data when executing use case`() {
        var result: Either<Failure, MyType>? = null

        val params = MyParams("TestParam")
        val onResult = { myResult: Either<Failure, MyType> -> result = myResult }

        whenever(runBlocking { useCase(TestCoroutineScope(), params, onResult) })
                .then { result shouldEqual Either.Success(MyType(typeTest)) }
    }

    data class MyType(val name: String)
    data class MyParams(val name: String)
}
