package me.ryansimon.yelpfusion.business

import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import me.ryansimon.yelpfusion.feature.business.BusinessesApi
import me.ryansimon.yelpfusion.feature.business.BusinessesRepository
import me.ryansimon.yelpfusion.feature.business.BusinessesResponse
import me.ryansimon.yelpfusion.network.Either
import org.amshove.kluent.shouldEqual
import org.junit.Test
import retrofit2.Call
import retrofit2.Response

/**
 * Test class for [BusinessesRepository]
 *
 * @author Ryan Simon
 */
class BusinessesRepositoryTest {

    @Test
    fun `Should get businesses response when search term and location provided`() {
        // given
        val searchTerm = "Italian"
        val location = "Irvine, CA"
        val businessesResponse = BusinessesResponse()
        val mockBusinessesApi: BusinessesApi = mock()
        val mockBusinessesResponse: Response<BusinessesResponse> = mock()
        val mockBusinessesCall: Call<BusinessesResponse> = mock()
        val businessesRepository = BusinessesRepository(mockBusinessesApi)
        given { mockBusinessesResponse.body() }.willReturn(businessesResponse)
        given { mockBusinessesCall.execute() }.willReturn(mockBusinessesResponse)
        given { mockBusinessesApi.search(searchTerm, location) }.willReturn(mockBusinessesCall)

        // when
        val response = businessesRepository.search(searchTerm, location)

        // then
        verify(mockBusinessesApi).search(searchTerm, location)
        response shouldEqual Either.Success(businessesResponse)
    }
}