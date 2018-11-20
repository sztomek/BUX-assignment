package hu.sztomek.buxassignment.device.api

import hu.sztomek.buxassignment.domain.model.ConnectivityStatus
import io.reactivex.Flowable

interface ConnectivityApi {

    fun getConnectivityStatus(): Flowable<ConnectivityStatus>

}