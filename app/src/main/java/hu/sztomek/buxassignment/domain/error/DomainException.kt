package hu.sztomek.buxassignment.domain.error

sealed class DomainException(message: String) : Throwable(message) {

    data class GeneralDomainException(override val message: String) : DomainException(message)

}