package hu.sztomek.buxassignment.presentation.common

interface MessageHolder {

    val message: String

}

sealed class UiError : MessageHolder {

    data class GeneralUiError(override val message: String) : UiError()
    data class RestCommunicationError(override val message: String) : UiError()
    data class HttpError(override val message: String, val details: String? = null) : UiError()
    data class SocketClosedError(override val message: String) : UiError()

}