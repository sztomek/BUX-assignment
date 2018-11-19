package hu.sztomek.buxassignment.data.model.ws

class WebSocketSubscriptionMessage(subscribeToIds: List<String>, unsubscribeFromIds: List<String>) {

    val subscribeTo: List<String> = subscribeToIds.map { it -> "trading.product.$it" }
    val unsubscribeFrom: List<String> = unsubscribeFromIds.map { it -> "trading.product.$it" }

}