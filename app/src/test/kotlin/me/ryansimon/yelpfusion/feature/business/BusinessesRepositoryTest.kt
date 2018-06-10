package me.ryansimon.yelpfusion.feature.business

import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import me.ryansimon.yelpfusion.feature.business.BusinessFailure.*
import me.ryansimon.yelpfusion.network.Either
import me.ryansimon.yelpfusion.network.Failure.*
import me.ryansimon.yelpfusion.network.InternetConnectionHandler
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

/**
 * Test class for [BusinessesRepository]
 *
 * @author Ryan Simon
 */
class BusinessesRepositoryTest {

    private lateinit var mockBusinessesApi: BusinessesApi
    private lateinit var mockBusinessesResponse: Response<BusinessesResponse>
    private lateinit var mockBusinessesCall: Call<BusinessesResponse>
    private lateinit var mockInternetConnectionHandler: InternetConnectionHandler
    private lateinit var businessesRepository: BusinessesRepository

    @Before
    fun setup() {
        mockBusinessesApi = mock()
        mockBusinessesResponse = mock()
        mockBusinessesCall = mock()
        mockInternetConnectionHandler = mock()
        businessesRepository = BusinessesRepository(mockBusinessesApi, mockInternetConnectionHandler)
    }

    @Test
    fun `Should get businesses response when search term and location provided`() {
        // given
        val businessesResponse = BUSINESS_RESPONSE
        given { mockInternetConnectionHandler.isConnected }.willReturn(true)
        given { mockBusinessesResponse.isSuccessful }.willReturn(true)
        given { mockBusinessesResponse.body() }.willReturn(businessesResponse)
        given { mockBusinessesCall.execute() }.willReturn(mockBusinessesResponse)
        given { mockBusinessesApi.search(VALID_SEARCH_TERM, VALID_LOCATION, NUM_RESULTS, NUM_RESULTS_TO_SKIP) }.willReturn(mockBusinessesCall)

        // when
        val response = businessesRepository.search(VALID_SEARCH_TERM, VALID_LOCATION)

        // then
        verify(mockBusinessesApi).search(VALID_SEARCH_TERM, VALID_LOCATION, NUM_RESULTS, NUM_RESULTS_TO_SKIP)
        response shouldEqual Either.Success(businessesResponse)
    }

    @Test
    fun `Businesses search should use results num and results to skip default values`() {
        // given
        given { mockInternetConnectionHandler.isConnected }.willReturn(true)
        given { mockBusinessesApi.search(VALID_SEARCH_TERM, VALID_LOCATION, NUM_RESULTS, NUM_RESULTS_TO_SKIP) }.willReturn(mockBusinessesCall)

        // when
        businessesRepository.search(VALID_SEARCH_TERM, VALID_LOCATION)

        // then
        verify(mockBusinessesApi).search(VALID_SEARCH_TERM, VALID_LOCATION, 20, 0)
    }

    @Test
    fun `Businesses search should return search parameters invalid failure when query params are invalid`() {
        // given
        given { mockInternetConnectionHandler.isConnected }.willReturn(true)

        // when
        val response = businessesRepository.search(VALID_SEARCH_TERM, VALID_LOCATION, 20, 1000)

        // then
        response shouldBeInstanceOf Either::class
        response.isError shouldEqualTo true
        response.either({ failure -> failure shouldBeInstanceOf SearchParametersAreInvalid::class }, {})
        verifyZeroInteractions(mockBusinessesApi)
    }

    @Test
    fun `Businesses search should return no network connection error when Internet is not available`() {
        // given
        given { mockInternetConnectionHandler.isConnected }.willReturn(false)

        // when
        val response = businessesRepository.search(VALID_SEARCH_TERM, VALID_LOCATION)

        // then
        response shouldBeInstanceOf Either::class
        response.isError shouldEqualTo true
        response.either({ failure -> failure shouldBeInstanceOf NoNetworkConnection::class }, {})
        verifyZeroInteractions(mockBusinessesApi)
    }

    @Test
    fun `Businesses search should return server error when API request is unsuccessful`() {
        // given
        given { mockInternetConnectionHandler.isConnected }.willReturn(true)
        given { mockBusinessesResponse.isSuccessful }.willReturn(false)
        given { mockBusinessesCall.execute() }.willReturn(mockBusinessesResponse)
        given { mockBusinessesApi.search(VALID_SEARCH_TERM, VALID_LOCATION, NUM_RESULTS, NUM_RESULTS_TO_SKIP) }.willReturn(mockBusinessesCall)

        // when
        val response = businessesRepository.search(VALID_SEARCH_TERM, VALID_LOCATION)

        // then
        verify(mockBusinessesApi).search(VALID_SEARCH_TERM, VALID_LOCATION, NUM_RESULTS, NUM_RESULTS_TO_SKIP)
        response shouldBeInstanceOf Either::class
        response.isError shouldEqualTo true
        response.either({ failure -> failure shouldBeInstanceOf ServerError::class }, {})
    }

    @Test
    fun `Businesses search should handle exceptions`() {
        // given
        given { mockInternetConnectionHandler.isConnected }.willReturn(true)
        given { mockBusinessesResponse.isSuccessful }.willReturn(false)
        given { mockBusinessesCall.execute() }.willThrow(IOException())
        given { mockBusinessesApi.search(VALID_SEARCH_TERM, VALID_LOCATION, NUM_RESULTS, NUM_RESULTS_TO_SKIP) }.willReturn(mockBusinessesCall)

        // when
        val response = businessesRepository.search(VALID_SEARCH_TERM, VALID_LOCATION)

        // then
        verify(mockBusinessesApi).search(VALID_SEARCH_TERM, VALID_LOCATION, NUM_RESULTS, NUM_RESULTS_TO_SKIP)
        response shouldBeInstanceOf Either::class
        response.isError shouldEqualTo true
        response.either({ failure -> failure shouldBeInstanceOf ServerError::class }, {})
    }
}