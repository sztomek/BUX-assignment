package hu.sztomek.buxassignment.domain.interactor

import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.data.DataRepository
import hu.sztomek.buxassignment.domain.model.NoOpDomainModel
import io.reactivex.Flowable
import javax.inject.Inject

class DisconnectInteractor @Inject constructor(private val dataRepository: DataRepository)
    : Interactor<Action.StopUpdates, NoOpDomainModel> {

    override fun execute(action: Action.StopUpdates): Flowable<NoOpDomainModel> {
        return dataRepository.disconnectLiveUpdates()
            .andThen(Flowable.just(NoOpDomainModel))
    }

}