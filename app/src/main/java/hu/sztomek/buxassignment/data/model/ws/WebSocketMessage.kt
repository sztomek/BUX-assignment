package hu.sztomek.buxassignment.data.model.ws

import hu.sztomek.buxassignment.data.model.common.ErrorDataModel

sealed class WebSocketMessage<T> {

    abstract val body: T

    data class ConnectedMessage(override val body: ConnectedDataModel) : WebSocketMessage<ConnectedDataModel>()
    data class FailedToConnectMessage(override val body: ErrorDataModel) : WebSocketMessage<ErrorDataModel>()
    data class TradingQuoteMessage(override val body: TradingQuoteDataModel) : WebSocketMessage<TradingQuoteDataModel>()
    data class SubscriptionReadbackMessage(override val body: WebSocketSubscriptionMessage) : WebSocketMessage<WebSocketSubscriptionMessage>()

}