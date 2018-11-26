package hu.sztomek.buxassignment.domain.interactor

import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.data.DataRepository
import hu.sztomek.buxassignment.domain.model.Subscription
import io.reactivex.Flowable
import javax.inject.Inject

class UpdateSubscriptionInteractor @Inject constructor(private val dataRepository: DataRepository) :
    Interactor<Action.UpdateSubscriptions, Subscription> {

    override fun execute(action: Action.UpdateSubscriptions): Flowable<Subscription> {
        return dataRepository.connectLiveUpdates()
            .andThen(
                dataRepository.updateSubscription(Subscription(action.subscribeTo, action.unsubscribeFrom))
                    .andThen(Flowable.just(Subscription(action.subscribeTo, action.unsubscribeFrom)))
        )
    }
}