package hu.sztomek.buxassignment.data.model.ws

abstract class WebSocketMessage<T> {

    var t: String? = null
    var body: T? = null

}