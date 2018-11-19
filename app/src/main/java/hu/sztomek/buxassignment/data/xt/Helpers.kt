package hu.sztomek.buxassignment.data.xt

import hu.sztomek.buxassignment.data.model.rest.ResponseDataModel

private const val HTTP_OK = 200
private const val HTTP_3XX = 300

fun ResponseDataModel<Any?>.isSuccessful(): Boolean {
    return httpStatus in HTTP_OK..HTTP_3XX && error == null
}