package hu.sztomek.buxassignment.data

import hu.sztomek.buxassignment.domain.data.DataRepository
import hu.sztomek.buxassignment.domain.model.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class DataRepositoryImpl : DataRepository {

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

    override fun getProductDetails(selectableProduct: ISelectableProduct): Single<ProductDetails> {
        return Single.never()
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