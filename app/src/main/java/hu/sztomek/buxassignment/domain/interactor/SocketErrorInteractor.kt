package hu.sztomek.buxassignment.domain.interactor

import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.data.DataRepository
import hu.sztomek.buxassignment.domain.error.DomainException
import io.reactivex.Flowable
import javax.inject.Inject

class SocketErrorInteractor @Inject constructor(private val dataRepository: DataRepository)
    : Interactor<Action.GetSocketErrors, DomainException.WebSocketDomainException> {

    override fun execute(action: Action.GetSocketErrors): Flowable<DomainException.WebSocketDomainException> {
        return dataRepository.socketErrors()
    }

}