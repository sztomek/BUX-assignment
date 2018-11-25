package hu.sztomek.buxassignment.domain.interactor

import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.data.DataRepository
import hu.sztomek.buxassignment.domain.model.DomainModel
import io.reactivex.Flowable
import javax.inject.Inject

class DisconnectInteractor @Inject constructor(private val dataRepository: DataRepository)
    : Interactor<Action.StopUpdates, DomainModel> {

    override fun execute(action: Action.StopUpdates): Flowable<DomainModel> {
        return dataRepository.disconnectLiveUpdates()
            .andThen(Flowable.empty<DomainModel>())
    }

}