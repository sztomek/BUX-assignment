package hu.sztomek.buxassignment.data

import com.google.gson.JsonParseException
import hu.sztomek.buxassignment.data.api.RestApi
import hu.sztomek.buxassignment.data.api.WsApi
import hu.sztomek.buxassignment.data.converter.toDomain
import hu.sztomek.buxassignment.data.error.RetrofitException
import hu.sztomek.buxassignment.data.error.WebSocketException
import hu.sztomek.buxassignment.data.model.rest.PriceDataModel
import hu.sztomek.buxassignment.data.model.rest.ProductDetailsDataModel
import hu.sztomek.buxassignment.domain.error.DomainException
import hu.sztomek.buxassignment.domain.model.ISelectableProduct
import hu.sztomek.buxassignment.domain.model.SelectableProduct
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.io.IOException

@RunWith(JUnit4::class)
class DataRepositoryImplTest {

    companion object {
        private val DUMMY_PRODUCTS = listOf<ISelectableProduct>(
                SelectableProduct("Germany30", "sb26493"),
                SelectableProduct("US500", "sb26496"),
                SelectableProduct("EUR/USD", "sb26502"),
                SelectableProduct("Gold", "sb26500"),
                SelectableProduct("Apple", "sb26513"),
                SelectableProduct("Deutsche Bank", "sb28248")
            )

        private val DUMMY_PRODUCT_DETAIL = ProductDetailsDataModel()

        init {
            DUMMY_PRODUCT_DETAIL.symbol = "abc"
            DUMMY_PRODUCT_DETAIL.securityId = "sbabc"
            DUMMY_PRODUCT_DETAIL.displayName = "DummyStock"
            val closingPrice = PriceDataModel()
            closingPrice.currency = "USD"
            closingPrice.decimals = 2
            closingPrice.amount = "100.00"
            DUMMY_PRODUCT_DETAIL.closingPrice = closingPrice
            val currentPrice = PriceDataModel()
            currentPrice.currency = "USD"
            currentPrice.decimals = 2
            currentPrice.amount = "150.00"
            DUMMY_PRODUCT_DETAIL.currentPrice = currentPrice
        }
    }

    @Mock
    private lateinit var restApi: RestApi
    @Mock
    private lateinit var wsApi: WsApi

    private lateinit var dataRepositoryImpl: DataRepositoryImpl

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
        dataRepositoryImpl = DataRepositoryImpl(restApi, wsApi)
    }

    @Test
    fun `test getSelectableProducts returns dummy list and terminates without error`() {
        dataRepositoryImpl.getSelectableProducts().test()
            .assertValue { it.containsAll(DUMMY_PRODUCTS) }
            .assertNoErrors()
            .assertTerminated()
    }

    @Test
    fun `test getProductDetails relies on restApi`() {
        `when`(restApi.getDetails(anyString())).thenReturn(Single.just(DUMMY_PRODUCT_DETAIL))

        dataRepositoryImpl.getProductDetails("anything")
            .test()

        verify(restApi).getDetails(anyString())
    }

    @Test
    fun `test getProducts emits single ProductDetail and terminates`() {
        `when`(restApi.getDetails(anyString())).thenReturn(Single.just(DUMMY_PRODUCT_DETAIL))

        dataRepositoryImpl.getProductDetails("anything")
            .test()
            .assertValue(DUMMY_PRODUCT_DETAIL.toDomain())
            .assertNoErrors()
            .assertTerminated()

    }

    @Test
    fun `test getProducts signals domain error on random exception`() {
        `when`(restApi.getDetails(anyString())).thenReturn(Single.error(RuntimeException("fail")))

        dataRepositoryImpl.getProductDetails("anything")
            .test()
            .assertError { it is DomainException.GeneralDomainException }
            .assertTerminated()
    }

    @Test
    fun `test getProducts network error handling`() {
        `when`(restApi.getDetails(anyString())).thenReturn(Single.error(RetrofitException.NetworkException(IOException("bye"))))

        dataRepositoryImpl.getProductDetails("anything")
            .test()
            .assertError { it is DomainException.RestDomainException.CommunicationException }
            .assertTerminated()
    }

    @Test
    fun `test getProducts json error handling`() {
        `when`(restApi.getDetails(anyString())).thenReturn(Single.error(RetrofitException.JsonParseException(
            JsonParseException("fail")
        )))

        dataRepositoryImpl.getProductDetails("anything")
            .test()
            .assertError { it is DomainException.RestDomainException.MessageParseException }
            .assertTerminated()
    }

    @Test
    fun `test getProducts unknown error handling`() {
        `when`(restApi.getDetails(anyString())).thenReturn(Single.error(RetrofitException.UnknownException(
            java.lang.RuntimeException("fail")
        )))

        dataRepositoryImpl.getProductDetails("anything")
            .test()
            .assertError { it is DomainException.GeneralDomainException }
            .assertTerminated()
    }

    @Test
    fun `test connectLiveUpdates relies on WsApi`() {
        `when`(wsApi.connect()).thenReturn(Completable.complete())

        dataRepositoryImpl.connectLiveUpdates().test()

        verify(wsApi).connect()
    }

    @Test
    fun `test connectLiveUpdates happy case`() {
        `when`(wsApi.connect()).thenReturn(Completable.complete())

        dataRepositoryImpl.connectLiveUpdates().test()
            .assertNoErrors()
            .assertTerminated()
    }

    @Test
    fun `test connectLiveUpdates signals domain error on random exception`() {
        `when`(wsApi.connect()).thenReturn(Completable.error(java.lang.RuntimeException("bye")))

        dataRepositoryImpl.connectLiveUpdates().test()
            .assertError { it is DomainException.WebSocketDomainException.WebSocketUnknownError }
            .assertTerminated()
    }

    @Test
    fun `test connectLiveUpdates connection failed error handling`() {
        `when`(wsApi.connect()).thenReturn(Completable.error(WebSocketException.ConnectionFailedException("no reason", null)))

        dataRepositoryImpl.connectLiveUpdates().test()
            .assertError { it is DomainException.WebSocketDomainException.WebSocketConnectionFailed }
            .assertTerminated()
    }

    @Test
    fun `test connectLiveUpdates json error handling`() {
        `when`(wsApi.connect()).thenReturn(Completable.error(WebSocketException.JsonParseException(JsonParseException("fail"))))

        dataRepositoryImpl.connectLiveUpdates().test()
            .assertError { it is DomainException.WebSocketDomainException.MessageParseException }
            .assertTerminated()
    }


    @Test
    fun `test connectLiveUpdates unknown error handling`() {
        `when`(wsApi.connect()).thenReturn(Completable.error(WebSocketException.UnknownException(java.lang.RuntimeException("fail"))))

        dataRepositoryImpl.connectLiveUpdates().test()
            .assertError { it is DomainException.WebSocketDomainException.WebSocketUnknownError }
            .assertTerminated()
    }

}