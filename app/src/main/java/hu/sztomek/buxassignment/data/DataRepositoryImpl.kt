package hu.sztomek.buxassignment.data

import hu.sztomek.buxassignment.data.api.RestApi
import hu.sztomek.buxassignment.data.api.WsApi
import hu.sztomek.buxassignment.data.converter.toData
import hu.sztomek.buxassignment.data.converter.toDomain
import hu.sztomek.buxassignment.data.error.RetrofitException
import hu.sztomek.buxassignment.data.error.WebSocketException
import hu.sztomek.buxassignment.data.model.common.ErrorDataModel
import hu.sztomek.buxassignment.data.model.ws.TradingQuoteDataModel
import hu.sztomek.buxassignment.data.model.ws.WebSocketMessage
import hu.sztomek.buxassignment.domain.data.DataRepository
import hu.sztomek.buxassignment.domain.error.DomainException
import hu.sztomek.buxassignment.domain.model.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import timber.log.Timber

class DataRepositoryImpl(
    private val restApi: RestApi,
    private val wsApi: WsApi
) : DataRepository {

    override fun getSelectableProducts(): Single<List<ISelectableProduct>> {
        return Single.fromCallable {
            listOf(
                SelectableProduct("Germany30", "sb26493"),
                SelectableProduct("US500", "sb26496"),
                SelectableProduct("EUR/USD", "sb26502"),
                SelectableProduct("Gold", "sb26500"),
                SelectableProduct("Apple", "sb26513"),
                SelectableProduct("Deutsche Bank", "sb28248")
            )
        }
    }

    override fun getProductDetails(productId: String): Single<ProductDetails> {
        return restApi.getDetails(productId)
            .onErrorResumeNext {
                val domainException = if (it is RetrofitException) {
                    parseProductDetailsError(it)
                } else {
                    DomainException.GeneralDomainException(it.message)
                }

                Single.error(domainException)
            }
            .map { it.toDomain() }
    }

    private fun parseProductDetailsError(retrofitException: RetrofitException): DomainException {
        return when (retrofitException) {
            is RetrofitException.HttpException -> {
                try {
                    val parsedError = retrofitException.getErrorBodyAs(ErrorDataModel::class.java)
                    when (parsedError) {
                        null -> DomainException.RestDomainException.HttpException(retrofitException.message)
                        else -> DomainException.RestDomainException.HttpException(parsedError.developerMessage, parsedError.errorCode)
                    }
                } catch (e: Exception) {
                    Timber.d("Failed to parse error body: [$e]")
                    DomainException.RestDomainException.HttpException(e.message)
                }
            }
            is RetrofitException.NetworkException -> {
                DomainException.RestDomainException.CommunicationException(retrofitException.message)
            }
            is RetrofitException.JsonParseException -> {
                DomainException.RestDomainException.MessageParseException(retrofitException.message)
            }
            is RetrofitException.UnknownException -> {
                DomainException.GeneralDomainException(retrofitException.message)
            }
        }
    }

    private fun parseWsErrors(error: WebSocketException): DomainException.WebSocketDomainException {
        return when (error) {
            is WebSocketException.ConnectionFailedException -> {
                DomainException.WebSocketDomainException.WebSocketConnectionFailed(error.reason ?: error.message)
            }
            is WebSocketException.SubscriptionUpdateFailedException -> {
                DomainException.WebSocketDomainException.WebSocketSubscriptionFailed
            }
            is WebSocketException.ConnectionTerminatedException -> {
                DomainException.WebSocketDomainException.WebSocketConnectionTerminated(error.message)
            }
            is WebSocketException.JsonParseException -> {
                DomainException.WebSocketDomainException.MessageParseException(error.message)
            }
            is WebSocketException.UnknownException -> {
                DomainException.WebSocketDomainException.WebSocketUnknownError(error.message)
            }
        }
    }

    override fun connectLiveUpdates(): Completable {
        return wsApi.connect()
            .onErrorResumeNext(this::handleWsErrors)
    }

    private fun handleWsErrors(it: Throwable): Completable? {
        val domainException = if (it is WebSocketException) {
            parseWsErrors(it)
        } else {
            DomainException.WebSocketDomainException.WebSocketUnknownError(it.message)
        }

        return Completable.error(domainException)
    }

    override fun disconnectLiveUpdates(): Completable {
        return wsApi.disconnect()
    }

    override fun updateSubscription(subscription: Subscription): Completable {
        return wsApi.updateSubscription(subscription.toData())
            .onErrorResumeNext(this::handleWsErrors)
    }

    override fun latestPriceForProduct(product: ISelectableProduct): Flowable<PriceUpdate> {
        return wsApi.messages
            .filter { it.body is TradingQuoteDataModel }
            .map { it as WebSocketMessage.TradingQuoteMessage }
            .map { it.toDomain() }
            .filter { it.productIdentifier == product.identifier }
    }

    override fun socketErrors(): Flowable<DomainException.WebSocketDomainException> {
        return wsApi.errors.map(this::parseWsErrors)
    }
}