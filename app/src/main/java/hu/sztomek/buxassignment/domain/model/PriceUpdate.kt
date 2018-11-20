package hu.sztomek.buxassignment.domain.model

data class PriceUpdate(val productIdentifier: String,
                       val currentPrice: String,
                       val timestamp: Long,
                       val isLiveData: Boolean) : DomainModel