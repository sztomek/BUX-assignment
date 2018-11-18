package hu.sztomek.buxassignment.domain.usecase

import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.model.DomainModel
import io.reactivex.Flowable

interface UseCase<A: Action, M: DomainModel> {

    fun execute(action: A): Flowable<M>

}