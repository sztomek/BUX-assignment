package hu.sztomek.buxassignment.data.api

import hu.sztomek.buxassignment.data.model.rest.ProductDetailsDataModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface RestApi {

    @GET("core/21/products/{productId}")
    fun getDetails(@Path("productId") productId: String): Single<ProductDetailsDataModel>

}