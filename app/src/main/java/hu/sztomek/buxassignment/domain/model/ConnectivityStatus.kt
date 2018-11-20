package hu.sztomek.buxassignment.domain.model

sealed class ConnectivityStatus {

    object ONLINE : ConnectivityStatus()
    object OFFLINE : ConnectivityStatus()

}