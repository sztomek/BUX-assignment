package hu.sztomek.buxassignment.data.converter

import hu.sztomek.buxassignment.data.model.rest.PriceDataModel
import hu.sztomek.buxassignment.data.model.rest.ProductDetailsDataModel
import hu.sztomek.buxassignment.domain.model.Price
import hu.sztomek.buxassignment.domain.model.ProductDetails

fun PriceDataModel.toDomain() = Price(
    currency ?: "",
    amount ?: ""
)

fun ProductDetailsDataModel.toDomain() = ProductDetails(
    displayName ?: "",
    securityId ?: "",
    symbol ?: "",
    closingPriceDataModel?.toDomain() ?: Price("", ""),
    currentPriceDataModel?.toDomain() ?: Price("", "")
)