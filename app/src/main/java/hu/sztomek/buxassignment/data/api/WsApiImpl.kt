package hu.sztomek.buxassignment.data.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hu.sztomek.buxassignment.data.error.WebSocketException
import hu.sztomek.buxassignment.data.model.common.ErrorDataModel
import hu.sztomek.buxassignment.data.model.ws.WebSocketConnectionEvents
import hu.sztomek.buxassignment.data.model.ws.WebSocketMessage
import hu.sztomek.buxassignment.data.model.ws.WebSocketSubscriptionMessage
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.processors.FlowableProcessor
import io.reactivex.processors.PublishProcessor
import okhttp3.*
import okio.ByteString
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit

const val DEFAULT_WS_TIMEOUT_IN_SECONDS = 10L

class WsApiImpl(
    private val okHttpClient: OkHttpClient,
    private val request: Request,
    private val gson: Gson,
    private val timeout: Long = DEFAULT_WS_TIMEOUT_IN_SECONDS
) : WsApi {

    private val connectionEventProcessor: FlowableProcessor<WebSocketConnectionEvents> =
        BehaviorProcessor.createDefault(WebSocketConnectionEvents.DISCONNECTED)
    override val connectionEvents: Flowable<WebSocketConnectionEvents>
        get() = connectionEventProcessor
    private val messageProcessor: FlowableProcessor<WebSocketMessage<*>> = PublishProcessor.create()
    override val messages: Flowable<WebSocketMessage<*>>
        get() = messageProcessor
    private val errorProcessor: FlowableProcessor<WebSocketException> = PublishProcessor.create()
    override val errors: Flowable<WebSocketException>
        get() = errorProcessor
    private var webSocket: WebSocket? = null
    private val webSocketListener: WebSocketListener by lazy {
        object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Timber.d("onOpen response: [$response]")

                connectionEventProcessor.onNext(WebSocketConnectionEvents.CONNECTED)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Timber.d("onFailure response: [$response], throwable: [$t]")

                errorProcessor.onNext(WebSocketException.ConnectionTerminatedException(t as? IOException))
                internalDisconnect()
                connectionEventProcessor.onNext(WebSocketConnectionEvents.DISCONNECTED)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Timber.d("onClosing code: [$code], reason: [$reason]")
                connectionEventProcessor.onNext(WebSocketConnectionEvents.DISCONNECTING)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Timber.d("onMessage [$text]")

                try {
                    val fromJson = gson.fromJson<Any>(text, genericType<WebSocketMessage<*>>())
                    if (fromJson is WebSocketMessage<*>) {
                        messageProcessor.onNext(fromJson)
                    }
                } catch (e: Exception) {
                    Timber.d(e)
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Timber.d("onMessage [$bytes]")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Timber.d("onClosed code: [$code], reason: [$reason]")
                connectionEventProcessor.onNext(WebSocketConnectionEvents.DISCONNECTED)
            }
        }
    }

    override fun connect(): Completable {
        return connectionEventProcessor.take(1)
            .flatMapCompletable {
                if (it == WebSocketConnectionEvents.CONNECTED) {
                    Timber.d("Already connected")
                    Completable.complete()
                } else {
                    Timber.d("Connection status: [$it], attempting to connect...")
                    disconnect().andThen(Completable.fromAction {
                        webSocket = okHttpClient.newWebSocket(request, webSocketListener)
                    }).andThen(connectionEventProcessor.skip(1)
                        .take(1)
                        .flatMapCompletable {
                            if (it != WebSocketConnectionEvents.CONNECTED)
                                errorProcessor.take(1)
                                    .flatMapCompletable {
                                        Completable.error(
                                            WebSocketException.ConnectionFailedException(null, it)
                                        )
                                    }
                            else messageProcessor.filter { it is WebSocketMessage.ConnectedMessage || it is WebSocketMessage.FailedToConnectMessage }
                                .take(1)
                                .flatMapCompletable {
                                    if (it is WebSocketMessage.ConnectedMessage) Completable.complete()
                                    else Completable.error(
                                        WebSocketException.ConnectionFailedException((it.body as ErrorDataModel).message, null)
                                    )
                                }
                        }
                    ).timeout(timeout, TimeUnit.SECONDS)
                }
            }
    }

    override fun disconnect(): Completable {
        return Completable.fromAction {
            internalDisconnect()
        }
    }

    private fun internalDisconnect() {
        webSocket?.cancel()
        webSocket = null
    }

    override fun updateSubscription(message: WebSocketSubscriptionMessage): Completable {
        return connectionEventProcessor.take(1)
            .flatMapCompletable {
                if (it == WebSocketConnectionEvents.CONNECTED) Completable.fromAction {
                    webSocket?.send(
                        gson.toJson(
                            message
                        )
                    )
                }
                else Completable.error(WebSocketException.SubscriptionUpdateFailedException)
            }
    }

    private inline fun <reified T> genericType() = object : TypeToken<T>() {}.type
}