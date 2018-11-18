package hu.sztomek.buxassignment.presentation.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.operation.Operation
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {

    private val actionStream = PublishProcessor.create<Action>()
    private val disposables = CompositeDisposable()
    private var subscribedToActions = false

    val stateStream: MutableLiveData<UiState> = MutableLiveData()

    override fun onCleared() {
        disposables.dispose()
        subscribedToActions = false
        super.onCleared()
    }


    fun takeInitialState(initialState: UiState) {
        if (subscribedToActions) {
            throw IllegalStateException("Already called takeInitialState!")
        }

        stateStream.value = initialState

        disposables.add(
                actionStream
                        .observeOn(Schedulers.computation())
                        .compose(invokeActions())
                        .scan(stateStream.value!!, getReducerFunction())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    stateStream.value = it
                                },
                                {
                                    Timber.d(it)
                                }
                        )
        )
        subscribedToActions = true
    }

    fun sendAction(action: Action) {
        actionStream.onNext(action)
    }

    protected abstract fun invokeActions(): FlowableTransformer<Action, Operation>
    protected abstract fun getReducerFunction(): BiFunction<UiState, in Operation, UiState>
}