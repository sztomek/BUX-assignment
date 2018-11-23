package hu.sztomek.buxassignment.data.converter

import hu.sztomek.buxassignment.data.model.rest.PriceDataModel
import hu.sztomek.buxassignment.data.model.rest.ProductDetailsDataModel
import hu.sztomek.buxassignment.data.model.ws.TradingQuoteDataModel
import hu.sztomek.buxassignment.data.model.ws.WebSocketMessage
import hu.sztomek.buxassignment.data.model.ws.WebSocketSubscriptionMessage
import hu.sztomek.buxassignment.domain.model.Price
import hu.sztomek.buxassignment.domain.model.PriceUpdate
import hu.sztomek.buxassignment.domain.model.ProductDetails
import hu.sztomek.buxassignment.domain.model.Subscription

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

fun Subscription.toData() = WebSocketSubscriptionMessage(
    subscribeTo.map { it.identifier },
    unsubscribeFrom.map { it.identifier }
)

fun WebSocketMessage<TradingQuoteDataModel>.toDomain() = PriceUpdate(
    body?.securityId ?: "",
    body?.currentPrice ?: "",
    System.currentTimeMillis()
)