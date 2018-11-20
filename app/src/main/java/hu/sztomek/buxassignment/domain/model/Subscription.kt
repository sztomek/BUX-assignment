package hu.sztomek.buxassignment.domain.model

data class Subscription(val subscribeTo: List<ISelectableProduct>,
                        val unsubscribeFrom: List<ISelectableProduct>) : DomainModel