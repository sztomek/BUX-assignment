package hu.sztomek.buxassignment.domain.error

sealed class DomainException(message: String?) : Throwable(message) {

    data class GeneralDomainException(override val message: String? = null) : DomainException(message)
    data class CheckNetworkException(override val message: String? = null) : DomainException(message)
    data class HttpException(override val message: String? = null, val errorCode: String? = null) : DomainException(message)

}