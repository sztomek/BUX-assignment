package hu.sztomek.buxassignment.domain.interactor

import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.data.DataRepository
import hu.sztomek.buxassignment.domain.model.PriceUpdate
import io.reactivex.Flowable
import javax.inject.Inject

class GetPriceUpdatesInteractor @Inject constructor(private val dataRepository: DataRepository)
    : Interactor<Action.GetLatestPrice, PriceUpdate> {

    override fun execute(action: Action.GetLatestPrice): Flowable<PriceUpdate> {
        return dataRepository.latestPriceForProduct(action.product)
    }

}