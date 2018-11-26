package hu.sztomek.buxassignment.data.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import hu.sztomek.buxassignment.data.error.WebSocketException
import hu.sztomek.buxassignment.data.model.ws.WebSocketMessage
import timber.log.Timber
import java.lang.reflect.Type
import javax.inject.Inject

class WsMessageDeserializer @Inject constructor() : JsonDeserializer<WebSocketMessage<*>> {

    private companion object {
        private const val KEY_TYPE = "t"
        private const val KEY_BODY = "body"
        private const val KEY_SUBSCRIBE_TO = "subscribeTo"
        private const val KEY_UNSUBSCRIBE_FROM = "unsubscribeFrom"

        private const val TYPE_CONNECTED = "connect.connected"
        private const val TYPE_CONNECTION_FAILED = "connect.failed"
        private const val TYPE_TRADING_QUOTE = "trading.quote"
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): WebSocketMessage<*> {
        if (json == null) {
            throw WebSocketException.JsonParseException(JsonParseException("Failed to deserialize message"))
        }

        if (json.isJsonObject) {
            val asJsonObject = json.asJsonObject
            if (asJsonObject.has(KEY_TYPE)) {
                val messageType = asJsonObject[KEY_TYPE].asString
                return when (messageType) {
                    TYPE_CONNECTED -> {
                        WebSocketMessage.ConnectedMessage(deserializeAs(context!!, asJsonObject[KEY_BODY]))
                    }
                    TYPE_CONNECTION_FAILED -> {
                        WebSocketMessage.FailedToConnectMessage(deserializeAs(context!!, asJsonObject[KEY_BODY]))
                    }
                    TYPE_TRADING_QUOTE -> {
                        WebSocketMessage.TradingQuoteMessage(deserializeAs(context!!, asJsonObject[KEY_BODY]))
                    }
                    else -> {
                        throw WebSocketException.JsonParseException(JsonParseException("Failed to deserialize message: [$json]"))
                    }
                }
            } else if(asJsonObject.has(KEY_SUBSCRIBE_TO.toUpperCase()) && asJsonObject.has(KEY_UNSUBSCRIBE_FROM.toUpperCase())) {
                return WebSocketMessage.SubscriptionUpdateMessage(deserializeAs(context!!, asJsonObject))
            } else {
                throw WebSocketException.JsonParseException(JsonParseException("Failed to deserialize message: [$json]"))
            }
        } else {
            throw WebSocketException.JsonParseException(JsonParseException("Failed to deserialize message: [$json]"))
        }
    }

    private inline fun <reified T> deserializeAs(context: JsonDeserializationContext, element: JsonElement): T {
        return try {
            context.deserialize<T>(element, T::class.java)
        } catch (e: JsonParseException) {
            Timber.d("Failed to deserialize message: [$element]")
            throw WebSocketException.JsonParseException(e)
        }
    }
}