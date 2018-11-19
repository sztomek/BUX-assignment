package hu.sztomek.buxassignment.data.model.rest

import hu.sztomek.buxassignment.data.model.common.ErrorDataModel

abstract class ResponseDataModel<T> {

    var httpStatus: Int = 0
    var error: ErrorDataModel? = null
    var payload: T? = null

}