package hu.sztomek.buxassignment.domain.model

data class ProductDetails(override val displayName: String,
                          override val identifier: String,
                          val symbol: String,
                          val closingPrice: Price,
                          val currentPrice: Price) : ISelectableProduct, DomainModel