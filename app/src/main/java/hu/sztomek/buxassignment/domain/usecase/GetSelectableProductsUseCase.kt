package hu.sztomek.buxassignment.domain.usecase

import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.data.DataRepository
import hu.sztomek.buxassignment.domain.model.SelectableProducts
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class GetSelectableProductsUseCase @Inject constructor(private val dataRepository: DataRepository)
    : UseCase<Action.GetSelectableProducts, SelectableProducts> {

    override fun execute(action: Action.GetSelectableProducts): Flowable<SelectableProducts> {
        return dataRepository.getSelectableProducts()
            .map { SelectableProducts(it) }
            .toFlowable()
            .observeOn(Schedulers.io())
    }
}