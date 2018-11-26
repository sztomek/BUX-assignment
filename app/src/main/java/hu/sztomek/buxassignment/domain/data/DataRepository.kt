package hu.sztomek.buxassignment.domain.data

import hu.sztomek.buxassignment.domain.error.DomainException
import hu.sztomek.buxassignment.domain.model.ISelectableProduct
import hu.sztomek.buxassignment.domain.model.PriceUpdate
import hu.sztomek.buxassignment.domain.model.ProductDetails
import hu.sztomek.buxassignment.domain.model.Subscription
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface DataRepository {

    fun getSelectableProducts(): Single<List<ISelectableProduct>>
    fun getProductDetails(productId: String): Single<ProductDetails>
    fun connectLiveUpdates(): Completable
    fun disconnectLiveUpdates(): Completable
    fun updateSubscription(subscription: Subscription): Completable
    fun latestPriceForProduct(product: ISelectableProduct): Flowable<PriceUpdate>
    fun socketErrors(): Flowable<DomainException.WebSocketDomainException>

}