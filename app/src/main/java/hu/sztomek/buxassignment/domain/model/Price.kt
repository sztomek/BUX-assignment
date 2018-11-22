package hu.sztomek.buxassignment.domain.model

data class Price(val currency: String,
                 val amount: String,
                 val decimals: Int = 0) : DomainModel