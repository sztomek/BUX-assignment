package hu.sztomek.buxassignment.data.error

import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

class RetrofitException private constructor(
    message: String?,
    val url: String?,
    private val response: Response<*>?,
    val kind: Kind,
    exception: Throwable?,
    private val retrofit: Retrofit?
) : RuntimeException(message, exception) {

    fun <T> getErrorBodyAs(type: Class<T>): T? {
        val errorBody = response?.errorBody() ?: return null
        val converter = retrofit?.responseBodyConverter<T>(type, emptyArray())

        return converter?.convert(errorBody)
    }

    enum class Kind {
        NETWORK,
        HTTP,
        UNEXPECTED
    }

    companion object {

        fun httpError(url: String, response: Response<*>, retrofit: Retrofit): RetrofitException {
            val message = "${response.code()} ${response.message()}"
            return RetrofitException(message, url, response, Kind.HTTP, null, retrofit)
        }

        fun networkError(exception: IOException): RetrofitException {
            return RetrofitException(exception.message, null, null, Kind.NETWORK, exception, null)
        }

        fun unexpectedError(exception: Throwable): RetrofitException {
            return RetrofitException(exception.message, null, null, Kind.UNEXPECTED, exception, null)
        }
    }

}
