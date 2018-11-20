package hu.sztomek.buxassignment.domain.model

internal data class SelectableProduct(override val displayName: String,
                                      override val identifier: String) : ISelectableProduct

data class SelectableProducts(val products: List<ISelectableProduct>): DomainModel