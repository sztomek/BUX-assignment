package hu.sztomek.buxassignment.domain.operation

import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.error.DomainException
import hu.sztomek.buxassignment.domain.model.DomainModel

interface ActionHolder {
    val action: Action
}

sealed class Operation : ActionHolder {

    data class InProgress(override val action: Action) : Operation()
    data class Completed(override val action: Action, val result: DomainModel) : Operation()
    data class Failed(override val action: Action, val exception: DomainException) : Operation()

}