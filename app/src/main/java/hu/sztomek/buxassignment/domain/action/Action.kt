package hu.sztomek.buxassignment.domain.action

import hu.sztomek.buxassignment.domain.model.ISelectableProduct

sealed class Action {

    object GetSelectableProducts : Action()
    data class SelectProduct(val selectedProduct: ISelectableProduct) : Action()
    data class GetProductDetails(val productId: String) : Action()
    data class UpdateSubscriptions(val subscribeTo: List<ISelectableProduct>,
                                   val unsubscribeFrom: List<ISelectableProduct>) : Action()
    data class GetLatestPrice(val product: ISelectableProduct) : Action()
    object StopUpdates : Action()
    object GetSocketErrors : Action()
}