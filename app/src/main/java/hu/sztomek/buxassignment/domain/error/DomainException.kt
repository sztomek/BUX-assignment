package hu.sztomek.buxassignment.domain.error

import hu.sztomek.buxassignment.domain.model.DomainModel

sealed class DomainException(message: String?) : Throwable(message) {

    data class GeneralDomainException(override val message: String? = null) : DomainException(message)

    sealed class RestDomainException(override val message: String? = null) : DomainException(message) {
        data class CommunicationException(override val message: String? = null) : RestDomainException(message)
        data class HttpException(override val message: String? = null, val errorCode: String? = null) : RestDomainException(message)
        data class MessageParseException(override val message: String? = null) : RestDomainException(message)
    }

    sealed class WebSocketDomainException(override val message: String? = null) : DomainException(message), DomainModel {
        data class WebSocketConnectionFailed(override val message: String?) : WebSocketDomainException(message)
        object WebSocketSubscriptionFailed : WebSocketDomainException()
        data class WebSocketConnectionTerminated(override val message: String?) : WebSocketDomainException(message)
        data class WebSocketUnknownError(override val message: String?) : WebSocketDomainException(message)
        data class MessageParseException(override val message: String? = null) : WebSocketDomainException(message)
    }

}