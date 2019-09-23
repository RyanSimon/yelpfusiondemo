package me.ryansimon.yelpfusion.feature.business

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import me.ryansimon.yelpfusion.feature.business.data.BusinessRepositoryImpl
import me.ryansimon.yelpfusion.feature.business.domain.BusinessFailure.*
import me.ryansimon.yelpfusion.core.functional.Either
import me.ryansimon.yelpfusion.core.network.Failure.*
import me.ryansimon.yelpfusion.core.network.InternetConnectionHandler
import me.ryansimon.yelpfusion.feature.business.data.network.*
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

/**
 * @author Ryan Simon
 */
class BusinessRepositoryImplTest {

    private lateinit var mockBusinessesApi: BusinessesApi
    private lateinit var mockBusinessesResponse: Response<BusinessesResponse>
    private lateinit var mockBusinessesCall: Call<BusinessesResponse>
    private lateinit var mockBusinessReviewsResponse: Response<BusinessReviewsResponse>
    private lateinit var mockBusinessReviewsCall: Call<BusinessReviewsResponse>
    private lateinit var mockInternetConnectionHandler: InternetConnectionHandler
    private lateinit var mockBusinessMapper: BusinessMapper
    private lateinit var mockBusinessReviewMapper: BusinessReviewMapper
    private lateinit var businessesRepository: BusinessRepositoryImpl

    @Before
    fun setup() {
        mockBusinessesApi = mock()
        mockBusinessesResponse = mock()
        mockBusinessesCall = mock()
        mockBusinessReviewsResponse = mock()
        mockBusinessReviewsCall = mock()
        mockBusinessMapper = mock()
        mockBusinessReviewMapper = mock()
        mockInternetConnectionHandler = mock()
        businessesRepository = BusinessRepositoryImpl(
                mockBusinessesApi,
                mockInternetConnectionHandler,
                mockBusinessMapper,
                mockBusinessReviewMapper
        )
    }

    @Test
    fun `Should get businesses response when search term and location provided`() {
        // given
        val businessesResponse = BUSINESS_RESPONSE
        val businessList = listOf(BUSINESS)
        given { mockInternetConnectionHandler.isConnected }.willReturn(true)
        given { mockBusinessesResponse.isSuccessful }.willReturn(true)
        given { mockBusinessesResponse.body() }.willReturn(businessesResponse)
        given { mockBusinessesCall.execute() }.willReturn(mockBusinessesResponse)
        given { mockBusinessMapper.make(BUSINESS_RESPONSE) }.willReturn(businessList)
        given { mockBusinessesApi.search(VALID_SEARCH_TERM, VALID_LOCATION, NUM_RESULTS, NUM_RESULTS_TO_SKIP) }.willReturn(mockBusinessesCall)

        // when
        val response = businessesRepository.search(VALID_SEARCH_TERM, VALID_LOCATION, NUM_RESULTS, NUM_RESULTS_TO_SKIP)

        // then
        verify(mockBusinessesApi).search(VALID_SEARCH_TERM, VALID_LOCATION, NUM_RESULTS, NUM_RESULTS_TO_SKIP)
        response shouldEqual Either.Success(businessList)
    }

    @Test
    fun `Businesses search should use results num and results to skip default values`() {
        // given
        given { mockInternetConnectionHandler.isConnected }.willReturn(true)
        given { mockBusinessesApi.search(VALID_SEARCH_TERM, VALID_LOCATION, NUM_RESULTS, NUM_RESULTS_TO_SKIP) }.willReturn(mockBusinessesCall)

        // when
        businessesRepository.search(VALID_SEARCH_TERM, VALID_LOCATION, NUM_RESULTS, NUM_RESULTS_TO_SKIP)

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
        response.either({ failure -> failure shouldEqual SearchParametersAreInvalid }, {})
        verifyZeroInteractions(mockBusinessesApi)
    }

    @Test
    fun `Businesses search should return no network connection error when Internet is not available`() {
        // given
        given { mockInternetConnectionHandler.isConnected }.willReturn(false)

        // when
        val response = businessesRepository.search(VALID_SEARCH_TERM, VALID_LOCATION, NUM_RESULTS, NUM_RESULTS_TO_SKIP)

        // then
        response shouldBeInstanceOf Either::class
        response.isError shouldEqualTo true
        response.either({ failure -> failure shouldEqual NoNetworkConnection }, {})
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
        val response = businessesRepository.search(VALID_SEARCH_TERM, VALID_LOCATION, NUM_RESULTS, NUM_RESULTS_TO_SKIP)

        // then
        verify(mockBusinessesApi).search(VALID_SEARCH_TERM, VALID_LOCATION, NUM_RESULTS, NUM_RESULTS_TO_SKIP)
        response shouldBeInstanceOf Either::class
        response.isError shouldEqualTo true
        response.either({ failure -> failure shouldEqual ServerError }, {})
    }

    @Test
    fun `Businesses search should handle exceptions`() {
        // given
        given { mockInternetConnectionHandler.isConnected }.willReturn(true)
        given { mockBusinessesResponse.isSuccessful }.willReturn(false)
        given { mockBusinessesCall.execute() }.willThrow(IOException())
        given { mockBusinessesApi.search(VALID_SEARCH_TERM, VALID_LOCATION, NUM_RESULTS, NUM_RESULTS_TO_SKIP) }.willReturn(mockBusinessesCall)

        // when
        val response = businessesRepository.search(VALID_SEARCH_TERM, VALID_LOCATION, NUM_RESULTS, NUM_RESULTS_TO_SKIP)

        // then
        verify(mockBusinessesApi).search(VALID_SEARCH_TERM, VALID_LOCATION, NUM_RESULTS, NUM_RESULTS_TO_SKIP)
        response shouldBeInstanceOf Either::class
        response.isError shouldEqualTo true
        response.either({ failure -> failure shouldEqual ServerError }, {})
    }

    @Test
    fun `Should get business reviews response when business id provided`() {
        // given
        val businessReviewResponse = BUSINESS_REVIEW_RESPONSE
        val businessReviewList = listOf(BUSINESS_REVIEW)
        given { mockInternetConnectionHandler.isConnected }.willReturn(true)
        given { mockBusinessReviewsResponse.isSuccessful }.willReturn(true)
        given { mockBusinessReviewsResponse.body() }.willReturn(businessReviewResponse)
        given { mockBusinessReviewsCall.execute() }.willReturn(mockBusinessReviewsResponse)
        given { mockBusinessReviewMapper.make(businessReviewResponse) }.willReturn(businessReviewList)
        given { mockBusinessesApi.getBusinessReviews(BUSINESS_ID) }.willReturn(mockBusinessReviewsCall)

        // when
        val response = businessesRepository.getBusinessReviews(BUSINESS_ID)

        // then
        verify(mockBusinessesApi).getBusinessReviews(BUSINESS_ID)
        response shouldEqual Either.Success(businessReviewList)
    }

    @Test
    fun `Should return no network connection error when Internet is not available for Get Business Reviews`() {
        // given
        given { mockInternetConnectionHandler.isConnected }.willReturn(false)

        // when
        val response = businessesRepository.getBusinessReviews(BUSINESS_ID)

        // then
        response shouldBeInstanceOf Either::class
        response.isError shouldEqualTo true
        response.either({ failure -> failure shouldEqual NoNetworkConnection }, {})
        verifyZeroInteractions(mockBusinessesApi)
    }

    @Test
    fun `Get Business Reviews should return server error when API request is unsuccessful`() {
        // given
        given { mockInternetConnectionHandler.isConnected }.willReturn(true)
        given { mockBusinessReviewsResponse.isSuccessful }.willReturn(false)
        given { mockBusinessReviewsCall.execute() }.willReturn(mockBusinessReviewsResponse)
        given { mockBusinessesApi.getBusinessReviews(BUSINESS_ID) }.willReturn(mockBusinessReviewsCall)

        // when
        val response = businessesRepository.getBusinessReviews(BUSINESS_ID)

        // then
        verify(mockBusinessesApi).getBusinessReviews(BUSINESS_ID)
        response shouldBeInstanceOf Either::class
        response.isError shouldEqualTo true
        response.either({ failure -> failure shouldEqual ServerError }, {})
    }

    @Test
    fun `Get Business Reviews should handle exceptions`() {
        // given
        given { mockInternetConnectionHandler.isConnected }.willReturn(true)
        given { mockBusinessReviewsResponse.isSuccessful }.willReturn(false)
        given { mockBusinessReviewsCall.execute() }.willThrow(IOException())
        given { mockBusinessesApi.getBusinessReviews(BUSINESS_ID) }.willReturn(mockBusinessReviewsCall)

        // when
        val response = businessesRepository.getBusinessReviews(BUSINESS_ID)

        // then
        verify(mockBusinessesApi).getBusinessReviews(BUSINESS_ID)
        response shouldBeInstanceOf Either::class
        response.isError shouldEqualTo true
        response.either({ failure -> failure shouldEqual ServerError }, {})
    }
}