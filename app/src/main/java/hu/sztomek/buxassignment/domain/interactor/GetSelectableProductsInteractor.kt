package hu.sztomek.buxassignment.domain.interactor

import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.data.DataRepository
import hu.sztomek.buxassignment.domain.model.SelectableProducts
import io.reactivex.Flowable
import javax.inject.Inject

class GetSelectableProductsInteractor @Inject constructor(private val dataRepository: DataRepository)
    : Interactor<Action.GetSelectableProducts, SelectableProducts> {

    override fun execute(action: Action.GetSelectableProducts): Flowable<SelectableProducts> {
        return dataRepository.getSelectableProducts()
            .toFlowable()
            .map { SelectableProducts(it) }
    }
}