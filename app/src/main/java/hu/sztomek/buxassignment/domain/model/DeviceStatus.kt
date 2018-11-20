package hu.sztomek.buxassignment.domain.model

sealed class DeviceStatus {

    object ONLINE : DeviceStatus()
    object OFFLINE : DeviceStatus()

}