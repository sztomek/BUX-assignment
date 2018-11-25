package hu.sztomek.buxassignment.data.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import hu.sztomek.buxassignment.data.model.ws.WebSocketMessage
import java.lang.reflect.Type
import javax.inject.Inject

class WsMessageDeserializer @Inject constructor() : JsonDeserializer<WebSocketMessage<*>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): WebSocketMessage<*> {
        if (json == null) {
            throw JsonParseException("Failed to deserialize message")
        }

        if (json.isJsonObject) {
            val asJsonObject = json.asJsonObject
            if (asJsonObject.has("t")) {
                val messageType = asJsonObject["t"].asString
                return when (messageType) {
                    "connect.connected" -> {
                        WebSocketMessage.ConnectedMessage(deserializeAs(context!!, asJsonObject["body"]))
                    }
                    "connect.failed" -> {
                        WebSocketMessage.FailedToConnectMessage(deserializeAs(context!!, asJsonObject["body"]))
                    }
                    "trading.quote" -> {
                        WebSocketMessage.TradingQuoteMessage(deserializeAs(context!!, asJsonObject["body"]))
                    }
                    else -> {
                        throw JsonParseException("Failed to deserialize message: [$json]")
                    }
                }
            } else if(asJsonObject.has("subscribeTo".toUpperCase()) && asJsonObject.has("unsubscribeFrom".toUpperCase())) {
                return WebSocketMessage.SubscriptionReadbackMessage(deserializeAs(context!!, asJsonObject))
            } else {
                throw JsonParseException("Failed to deserialize message: [$json]")
            }
        } else {
            throw JsonParseException("Failed to deserialize message: [$json]")
        }
    }

    private inline fun <reified T> deserializeAs(context: JsonDeserializationContext, element: JsonElement): T {
        return context.deserialize<T>(element, T::class.java)
    }
}