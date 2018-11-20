package hu.sztomek.buxassignment.domain.interactor

import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.model.DomainModel
import io.reactivex.Flowable

interface Interactor<A: Action, M: DomainModel> {

    fun execute(action: A): Flowable<M>

}