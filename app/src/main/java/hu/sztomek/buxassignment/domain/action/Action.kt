package hu.sztomek.buxassignment.domain.action

import hu.sztomek.buxassignment.domain.model.ISelectableProduct

sealed class Action {

    object GetSelectableProducts : Action()
    object GetDeviceStatus : Action()
    data class GetProductDetails(val product: ISelectableProduct) : Action()
    object GetSubscriptions : Action()
    data class UpdateSubscriptions(val subscribeTo: List<ISelectableProduct>,
                                   val unsubscribeFrom: List<ISelectableProduct>) : Action()
    data class GetLatestPrice(val product: ISelectableProduct) : Action()
}