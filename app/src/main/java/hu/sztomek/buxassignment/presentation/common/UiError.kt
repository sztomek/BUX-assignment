package hu.sztomek.buxassignment.presentation.common

interface MessageHolder {

    val message: String

}

sealed class UiError : MessageHolder {

    data class GeneralUiError(override val message: String) : UiError()

}