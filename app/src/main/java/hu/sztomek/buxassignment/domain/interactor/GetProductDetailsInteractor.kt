package hu.sztomek.buxassignment.domain.interactor

import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.data.DataRepository
import hu.sztomek.buxassignment.domain.model.ProductDetails
import io.reactivex.Flowable
import javax.inject.Inject

class GetProductDetailsInteractor @Inject constructor(private val repository: DataRepository): Interactor<Action.GetProductDetails, ProductDetails> {

    override fun execute(action: Action.GetProductDetails): Flowable<ProductDetails> {
        return repository.getProductDetails(action.productId)
            .toFlowable()
    }

}