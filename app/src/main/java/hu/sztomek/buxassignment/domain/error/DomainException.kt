package hu.sztomek.buxassignment.domain.error

sealed class DomainException(message: String?) : Throwable(message) {

    data class GeneralDomainException(override val message: String? = null) : DomainException(message)
    data class CommunicationException(override val message: String? = null) : DomainException(message)
    data class HttpException(override val message: String? = null, val errorCode: String? = null) : DomainException(message)
    data class WebSocketSubscriptionFailed(override val message: String?, val errorCode: String? = null) : DomainException(message)
    data class WebSocketUnknownMessageError(override val message: String?) : DomainException(message)

}