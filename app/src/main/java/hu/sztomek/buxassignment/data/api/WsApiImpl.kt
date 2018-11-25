package hu.sztomek.buxassignment.data.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hu.sztomek.buxassignment.data.model.common.ErrorDataModel
import hu.sztomek.buxassignment.data.model.ws.WebSocketConnectionEvents
import hu.sztomek.buxassignment.data.model.ws.WebSocketMessage
import hu.sztomek.buxassignment.data.model.ws.WebSocketSubscriptionMessage
import hu.sztomek.buxassignment.domain.error.DomainException
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.processors.FlowableProcessor
import io.reactivex.processors.PublishProcessor
import okhttp3.*
import okio.ByteString
import timber.log.Timber

class WsApiImpl(private val okHttpClient: OkHttpClient, private val gson: Gson) : WsApi {

    private val connectionEventProcessor: FlowableProcessor<WebSocketConnectionEvents> =
        BehaviorProcessor.createDefault(WebSocketConnectionEvents.DISCONNECTED)
    override val connectionEvents: Flowable<WebSocketConnectionEvents>
        get() = connectionEventProcessor
    private val messageProcessor: FlowableProcessor<WebSocketMessage<*>> = PublishProcessor.create()
    override val messages: Flowable<WebSocketMessage<*>>
        get() = messageProcessor
    private val errorProcessor: FlowableProcessor<ErrorDataModel> = PublishProcessor.create()
    override val errors: Flowable<ErrorDataModel>
        get() = errorProcessor
    private var webSocket: WebSocket? = null
    private val webSocketListener: WebSocketListener by lazy {
        object: WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Timber.d("onOpen [$response]")

                connectionEventProcessor.onNext(WebSocketConnectionEvents.CONNECTED)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Timber.d("onFailure [$response], [$t]")
                val toString = response?.body()?.toString()
                val fromJson = gson.fromJson<WebSocketMessage.FailedToConnectMessage>(
                    toString,
                    genericType<WebSocketMessage.FailedToConnectMessage>()
                )

                errorProcessor.onNext(ErrorDataModel()) // TODO
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
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
                connectionEventProcessor.onNext(WebSocketConnectionEvents.DISCONNECTED)
            }
        }
    }

    override fun connect(): Completable {
        return Completable.fromAction {
            webSocket = okHttpClient.newWebSocket(Request.Builder()
                .addHeader("Accept-Language", "nl-NL,en;q=0.8")
                .addHeader(
                    "Authorization",
                    "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyZWZyZXNoYWJsZSI6ZmFsc2UsInN1YiI6ImJiMGNkYTJiLWExMGUtNGVkMy1hZDVhLTBmODJiNGMxNTJjNCIsImF1ZCI6ImJldGEuZ2V0YnV4LmNvbSIsInNjcCI6WyJhcHA6bG9naW4iLCJydGY6bG9naW4iXSwiZXhwIjoxODIwODQ5Mjc5LCJpYXQiOjE1MDU0ODkyNzksImp0aSI6ImI3MzlmYjgwLTM1NzUtNGIwMS04NzUxLTMzZDFhNGRjOGY5MiIsImNpZCI6Ijg0NzM2MjI5MzkifQ.M5oANIi2nBtSfIfhyUMqJnex-JYg6Sm92KPYaUL9GKg"
                )
                .url("http://192.168.1.3:8080/subscriptions/me")
                .build(), webSocketListener)
        }.andThen(connectionEventProcessor.skip(1)
            .take(1)
            .flatMapCompletable {
                if (it == WebSocketConnectionEvents.CONNECTED) Completable.complete()
                else Completable.error(DomainException.WebSocketUnknownMessageError("alma"))
            }
        )
    }

    override fun disconnect(): Completable {
        return Completable.fromAction {
            webSocket?.cancel()
            webSocket = null
        }
    }

    override fun updateSubscription(message: WebSocketSubscriptionMessage): Completable {
        return Completable.fromAction { webSocket?.send(gson.toJson(message)) }
    }

    private inline fun <reified T> genericType() = object : TypeToken<T>() {}.type
}