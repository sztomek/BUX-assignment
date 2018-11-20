package hu.sztomek.buxassignment.data

import hu.sztomek.buxassignment.domain.data.DataRepository
import hu.sztomek.buxassignment.domain.model.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class DataRepositoryImpl : DataRepository {

    override fun getSelectableProducts(): Single<List<ISelectableProduct>> {
        return Single.just(
            listOf(
                SelectableProduct("Germany30", "sb26493"),
                SelectableProduct("US500", "sb26496"),
                SelectableProduct("EUR/USD", "sb26502"),
                SelectableProduct("Gold", "sb26500"),
                SelectableProduct("Apple", "sb26513"),
                SelectableProduct("Deutsche Bank","sb28248")
            )
        )
    }

    override fun getDeviceStatus(): Flowable<DeviceStatus> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getProductDetails(selectableProduct: ISelectableProduct): Single<ProductDetails> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSubscribedProducts(): Single<List<ISelectableProduct>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateSubscription(subscription: Subscription): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun latestPriceForProduct(product: ISelectableProduct): Flowable<PriceUpdate> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}