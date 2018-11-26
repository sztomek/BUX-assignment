package hu.sztomek.buxassignment.data.error

import java.io.IOException

sealed class WebSocketException(
    throwable: Throwable? = null
) : RuntimeException(throwable) {

    class ConnectionFailedException(val reason: String?, exception: Throwable?) : WebSocketException(exception)
    object SubscriptionUpdateFailedException : WebSocketException()
    class ConnectionTerminatedException(exception: IOException?) : WebSocketException(exception)
    class JsonParseException(exception: com.google.gson.JsonParseException) : WebSocketException(exception)
    class UnknownException(exception: Throwable) : WebSocketException(exception)

}