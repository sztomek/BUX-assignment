package hu.sztomek.buxassignment.data

import hu.sztomek.buxassignment.data.api.RestApi
import hu.sztomek.buxassignment.data.converter.toDomain
import hu.sztomek.buxassignment.data.error.RetrofitException
import hu.sztomek.buxassignment.data.model.common.ErrorDataModel
import hu.sztomek.buxassignment.domain.data.DataRepository
import hu.sztomek.buxassignment.domain.error.DomainException
import hu.sztomek.buxassignment.domain.model.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class DataRepositoryImpl(private val restApi: RestApi) : DataRepository {

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

    override fun getDeviceStatus(): Flowable<ConnectivityStatus> {
        return Flowable.empty<ConnectivityStatus>()
    }

    override fun getProductDetails(productId: String): Single<ProductDetails> {
        return restApi.getDetails(productId)
            .onErrorResumeNext {
                if (it is RetrofitException) {
                    Single.error(parseProductDetailsError(it))
                } else {
                    Single.error(DomainException.GeneralDomainException())
                }
            }
            .map { it.toDomain() }
    }

    private fun parseProductDetailsError(retrofitException: RetrofitException): DomainException {
        return when (retrofitException.kind) {
            RetrofitException.Kind.HTTP -> {
                val parsedError = retrofitException.getErrorBodyAs(ErrorDataModel::class.java)
                when(parsedError) {
                    null -> DomainException.HttpException()
                    else -> DomainException.HttpException(parsedError.developerMessage, parsedError.errorCode)
                }
            }
            RetrofitException.Kind.NETWORK -> {
                DomainException.CommunicationException()
            }
            RetrofitException.Kind.UNEXPECTED -> {
                DomainException.GeneralDomainException()
            }
        }
    }

    override fun getSubscribedProducts(): Single<List<ISelectableProduct>> {
        return Single.never()
    }

    override fun updateSubscription(subscription: Subscription): Completable {
        return Completable.never()
    }

    override fun latestPriceForProduct(product: ISelectableProduct): Flowable<PriceUpdate> {
        return Flowable.never()
    }
}