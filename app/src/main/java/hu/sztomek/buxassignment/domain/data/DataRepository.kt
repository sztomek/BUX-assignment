package hu.sztomek.buxassignment.domain.data

import hu.sztomek.buxassignment.domain.model.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface DataRepository {

    fun getSelectableProducts(): Single<List<ISelectableProduct>>
    fun getDeviceStatus(): Flowable<DeviceStatus>
    fun getProductDetails(selectableProduct: ISelectableProduct): Single<ProductDetails>
    fun getSubscribedProducts(): Single<List<ISelectableProduct>>
    fun updateSubscription(subscription: Subscription): Completable
    fun latestPriceForProduct(product: ISelectableProduct): Flowable<PriceUpdate>

}