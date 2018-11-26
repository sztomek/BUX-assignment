package hu.sztomek.buxassignment.data.error

import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

sealed class RetrofitException(
    exception: Throwable?
) : RuntimeException(exception) {

    class HttpException(private val response: Response<*>, private val retrofit: Retrofit?): RetrofitException(null) {
        fun <T> getErrorBodyAs(type: Class<T>): T? {
            val errorBody = response.errorBody() ?: return null
            val converter = retrofit?.responseBodyConverter<T>(type, emptyArray())

            return converter?.convert(errorBody)
        }
    }
    class NetworkException(originalException: IOException) : RetrofitException(originalException)
    class JsonParseException(jsonException: com.google.gson.JsonParseException?) : RetrofitException(jsonException)
    class UnknownException(exception: Throwable) : RetrofitException(exception)

}
