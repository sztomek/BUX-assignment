package hu.sztomek.buxassignment.presentation.common

import hu.sztomek.buxassignment.presentation.model.PersistableModel

internal interface DataHolder {
    val data: PersistableModel
}

sealed class UiState : DataHolder {

    data class IdleState(override val data: PersistableModel): UiState()
    data class LoadingState(val message: String, override val data: PersistableModel): UiState()
    data class ErrorState(val uiError: UiError, override val data: PersistableModel): UiState()

}