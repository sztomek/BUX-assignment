package hu.sztomek.buxassignment.data.api

import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import hu.sztomek.buxassignment.data.model.common.ErrorDataModel
import hu.sztomek.buxassignment.data.model.ws.TradingQuoteDataModel
import hu.sztomek.buxassignment.data.model.ws.WebSocketMessage
import hu.sztomek.buxassignment.data.model.ws.WebSocketSubscriptionMessage
import io.reactivex.Flowable

interface WebSocketApi {

    @Receive
    fun onConnectedEvent(): Flowable<WebSocketMessage<ErrorDataModel>>
    @Receive
    fun onTradingQuoteEvent(): Flowable<WebSocketMessage<TradingQuoteDataModel>>

    @Send
    fun updateSubscription(message: WebSocketSubscriptionMessage)

}