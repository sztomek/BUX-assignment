package hu.sztomek.buxassignment.data.error

import com.google.gson.JsonParseException
import hu.sztomek.buxassignment.R
import hu.sztomek.buxassignment.domain.scheduler.WorkSchedulers
import io.reactivex.Single
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException
import java.lang.reflect.Type
import javax.inject.Inject

class ErrorHandlingCallAdapterFactory @Inject constructor(workSchedulers: WorkSchedulers) : CallAdapter.Factory() {

    companion object {
         private fun convertThrowable(retrofit: Retrofit,
                                      throwable: Throwable): Exception {
             return when (throwable) {
                 is HttpException -> {
                     val response = throwable.response()
                     RetrofitException.HttpException(response, retrofit)
                 }
                 is IOException -> {
                     RetrofitException.NetworkException(throwable)
                 }
                 is JsonParseException -> {
                     RetrofitException.JsonParseException(throwable)
                 }
                 else -> {
                     RetrofitException.UnknownException(throwable)
                 }
             }
         }
    }

    private val rxCallAdapterFactory by lazy {
        RxJava2CallAdapterFactory.createWithScheduler(workSchedulers.io())
    }

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        val rawType = CallAdapter.Factory.getRawType(returnType)
        val originalCallAdapter = rxCallAdapterFactory.get(returnType, annotations, retrofit)

        return when(rawType) {
            Single::class.java -> {
                RxSingleCallAdapterWrapper(retrofit, originalCallAdapter as CallAdapter<R, Single<R>>)
            }
            else -> originalCallAdapter
        }
    }

    private abstract class RxBaseCallAdapterWrapper<R, T> constructor(
        internal val retrofit: Retrofit,
        internal val wrapped: CallAdapter<R, T>
    ) : CallAdapter<R, T> {

        override fun responseType(): Type {
            return wrapped.responseType()
        }
    }

    private class RxSingleCallAdapterWrapper<R>(
        retrofit: Retrofit,
        wrapped: CallAdapter<R, Single<R>>
    ) : RxBaseCallAdapterWrapper<R, Single<R>>(retrofit, wrapped) {

        override fun adapt(call: Call<R>): Single<R> {
            return wrapped.adapt(call)
                .onErrorResumeNext {
                    Single.error(convertThrowable(retrofit, it))
                }
        }
    }
}