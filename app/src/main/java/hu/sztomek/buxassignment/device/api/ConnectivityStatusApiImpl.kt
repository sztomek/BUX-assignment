package hu.sztomek.buxassignment.device.api

import hu.sztomek.buxassignment.domain.model.ConnectivityStatus
import io.reactivex.Flowable

class ConnectivityStatusApiImpl : ConnectivityApi {

    override fun getConnectivityStatus(): Flowable<ConnectivityStatus> {
        return Flowable.just(ConnectivityStatus.ONLINE)
    }

}