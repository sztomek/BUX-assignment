package hu.sztomek.buxassignment.domain.model

data class SelectableProduct(override val displayName: String,
                             override val identifier: String) : ISelectableProduct, DomainModel

data class SelectableProducts(val products: List<ISelectableProduct>): DomainModel