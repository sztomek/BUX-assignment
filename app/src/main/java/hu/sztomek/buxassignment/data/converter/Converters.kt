package hu.sztomek.buxassignment.data.converter

import hu.sztomek.buxassignment.data.model.rest.PriceDataModel
import hu.sztomek.buxassignment.data.model.rest.ProductDetailsDataModel
import hu.sztomek.buxassignment.domain.model.Price
import hu.sztomek.buxassignment.domain.model.ProductDetails

fun PriceDataModel.toDomain() = Price(
    currency ?: "",
    amount ?: "",
    decimals ?: 0
)

fun ProductDetailsDataModel.toDomain() = ProductDetails(
    displayName ?: "",
    securityId ?: "",
    symbol ?: "",
    closingPrice?.toDomain() ?: Price("", ""),
    currentPrice?.toDomain() ?: Price("", "")
)