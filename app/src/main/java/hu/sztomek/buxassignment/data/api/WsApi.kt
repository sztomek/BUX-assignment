package hu.sztomek.buxassignment.data.api

import hu.sztomek.buxassignment.data.model.common.ErrorDataModel
import hu.sztomek.buxassignment.data.model.ws.WebSocketConnectionEvents
import hu.sztomek.buxassignment.data.model.ws.WebSocketMessage
import hu.sztomek.buxassignment.data.model.ws.WebSocketSubscriptionMessage
import io.reactivex.Completable
import io.reactivex.Flowable

interface WsApi {

    val connectionEvents: Flowable<WebSocketConnectionEvents>
    val errors: Flowable<ErrorDataModel>
    val messages: Flowable<WebSocketMessage<*>>

    fun connect(): Completable
    fun disconnect(): Completable
    fun updateSubscription(message: WebSocketSubscriptionMessage): Completable

}