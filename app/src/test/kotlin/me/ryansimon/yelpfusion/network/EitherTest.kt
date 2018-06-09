package me.ryansimon.yelpfusion.network

import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Test

/**
 * Test class for [Either]
 *
 * @author Ryan Simon
 */
class EitherTest {

    @Test
    fun `Either Success should return correct type`() {
        val result = Either.Success("ironman")

        result shouldBeInstanceOf Either::class.java
        result.isSuccess shouldBe true
        result.isError shouldBe false
        result.either({},
                { right ->
                    right shouldBeInstanceOf String::class.java
                    right shouldBeEqualTo "ironman"
                })
    }

    @Test
    fun `Either Error should return correct type`() {
        val result = Either.Error("ironman")

        result shouldBeInstanceOf Either::class.java
        result.isError shouldBe true
        result.isSuccess shouldBe false
        result.either(
                { left ->
                    left shouldBeInstanceOf String::class.java
                    left shouldBeEqualTo "ironman"
                }, {})
    }
}