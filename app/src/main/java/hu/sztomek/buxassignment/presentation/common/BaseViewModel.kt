package hu.sztomek.buxassignment.presentation.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.operation.Operation
import hu.sztomek.buxassignment.domain.scheduler.WorkSchedulers
import io.reactivex.FlowableTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.processors.FlowableProcessor
import io.reactivex.processors.PublishProcessor
import timber.log.Timber

abstract class BaseViewModel(protected val workSchedulers: WorkSchedulers) : ViewModel() {

    private val actionStream: FlowableProcessor<Action> = PublishProcessor.create<Action>()
    private val disposables = CompositeDisposable()
    private var subscribedToActions = false

    val stateStream: MutableLiveData<UiState> = MutableLiveData()

    override fun onCleared() {
        disposables.dispose()
        subscribedToActions = false
        super.onCleared()
    }

    open fun takeInitialState(initialState: UiState) {
        if (subscribedToActions) {
            throw IllegalStateException("Already called takeInitialState!")
        }

        actionStream
            .observeOn(workSchedulers.io())
            .compose(invokeActions())
            .scan(stateStream.value ?: initialState, getReducerFunction())
            .observeOn(workSchedulers.ui())
            .subscribe(
                { stateStream.value = it },
                Timber::e
            ).apply {
                disposables.add(this)
                subscribedToActions = true
            }
    }

    fun sendAction(action: Action) {
        actionStream.onNext(action)
    }

    protected abstract fun invokeActions(): FlowableTransformer<Action, Operation>
    protected abstract fun getReducerFunction(): BiFunction<UiState, Operation, UiState>
}