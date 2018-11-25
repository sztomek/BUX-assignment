package hu.sztomek.buxassignment.presentation.screen.details

import hu.sztomek.buxassignment.R
import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.error.DomainException
import hu.sztomek.buxassignment.domain.interactor.DisconnectInteractor
import hu.sztomek.buxassignment.domain.interactor.GetPriceUpdatesInteractor
import hu.sztomek.buxassignment.domain.interactor.GetProductDetailsInteractor
import hu.sztomek.buxassignment.domain.interactor.UpdateSubscriptionInteractor
import hu.sztomek.buxassignment.domain.model.PriceUpdate
import hu.sztomek.buxassignment.domain.model.ProductDetails
import hu.sztomek.buxassignment.domain.model.Subscription
import hu.sztomek.buxassignment.domain.operation.Operation
import hu.sztomek.buxassignment.domain.resource.ResourceHelper
import hu.sztomek.buxassignment.domain.scheduler.WorkSchedulers
import hu.sztomek.buxassignment.presentation.common.BaseViewModel
import hu.sztomek.buxassignment.presentation.common.UiError
import hu.sztomek.buxassignment.presentation.common.UiState
import hu.sztomek.buxassignment.presentation.converter.toUiModel
import hu.sztomek.buxassignment.presentation.converter.updateLiveStatus
import hu.sztomek.buxassignment.presentation.converter.updatePrice
import hu.sztomek.buxassignment.presentation.model.ProductDetailsModel
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.functions.BiFunction
import timber.log.Timber
import javax.inject.Inject

class ProductDetailsViewModel @Inject constructor(
    workSchedulers: WorkSchedulers,
    private val resourceHelper: ResourceHelper,
    private val productDetailsInteractor: GetProductDetailsInteractor,
    private val updateSubscriptionInteractor: UpdateSubscriptionInteractor,
    private val getPriceUpdatesInteractor: GetPriceUpdatesInteractor,
    private val disconnectInteractor: DisconnectInteractor
) : BaseViewModel(workSchedulers) {

    override fun invokeActions(): FlowableTransformer<Action, Operation> {
        return FlowableTransformer { upstream ->
            Flowable.merge(
                upstream.ofType(Action.GetProductDetails::class.java)
                    .flatMap { action ->
                        productDetailsInteractor.execute(action)
                            .map { Operation.Completed(action, it) as Operation }
                            .startWith(Operation.InProgress(action))
                            .onErrorReturn {
                                Operation.Failed(action,
                                    if (it is DomainException) it
                                    else DomainException.GeneralDomainException(it.message ?: resourceHelper.getString(R.string.error_unknown_message))
                                )
                            }
                    },
                upstream.ofType(Action.UpdateSubscriptions::class.java)
                    .flatMap { action ->
                        updateSubscriptionInteractor.execute(action)
                            .map { Operation.Completed(action, it) as Operation }
                            .startWith(Operation.InProgress(action))
                            .onErrorReturn {
                                Operation.Failed(action,
                                    if (it is DomainException) it
                                    else DomainException.GeneralDomainException(it.message ?: resourceHelper.getString(R.string.error_unknown_message))
                                )
                            }
                    },
                upstream.ofType(Action.GetLatestPrice::class.java)
                    .flatMap { action ->
                        getPriceUpdatesInteractor.execute(action)
                            .map { Operation.Completed(action, it) as Operation }
                            .startWith(Operation.InProgress(action))
                            .onErrorReturn {
                                Operation.Failed(action,
                                    if (it is DomainException) it
                                    else DomainException.GeneralDomainException(it.message ?: resourceHelper.getString(R.string.error_unknown_message))
                                )
                            }
                    },
                upstream.ofType(Action.StopUpdates::class.java)
                    .flatMap { action ->
                        disconnectInteractor.execute(action)
                            .map { Operation.Completed(action, it) as Operation }
                            .startWith(Operation.InProgress(action))
                            .onErrorReturn {
                                Operation.Failed(action,
                                    if (it is DomainException) it
                                    else DomainException.GeneralDomainException(it.message ?: resourceHelper.getString(R.string.error_unknown_message))
                                )
                            }
                    }
            )
        }
    }

    override fun getReducerFunction(): BiFunction<UiState, Operation, UiState> {
        return BiFunction { oldState, operation ->
            when (operation) {
                is Operation.InProgress -> {
                    UiState.LoadingState(resourceHelper.getString(R.string.loading_message), oldState.data)
                }
                is Operation.Failed -> {
                    UiState.ErrorState(
                        parseDomainException(operation.exception), oldState.data
                    )
                }
                is Operation.Completed -> {
                    val productDetailsModel = oldState.data as ProductDetailsModel
                    UiState.IdleState(when (operation.result) {
                        is ProductDetails -> {
                            operation.result.toUiModel()
                        }
                        is Subscription -> {
                            val updateLiveStatus = productDetailsModel.updateLiveStatus(operation.result)
                            if (updateLiveStatus.liveUpdateEnabled) {
                                   sendAction(Action.GetLatestPrice(productDetailsModel.model!!))
                            }

                            updateLiveStatus
                        }
                        is PriceUpdate -> {
                            productDetailsModel.updatePrice(operation.result)
                        }
                        else -> {
                            Timber.d("Unhandled Operation.result type: ${operation.result.javaClass}")
                            oldState.data
                        }
                    })

                }
            }

        }
    }

    private fun parseDomainException(exception: DomainException): UiError {
        return when(exception) {
            is DomainException.CommunicationException -> {
                UiError.CommunicationError(resourceHelper.getString(R.string.error_network_message))
            }
            is DomainException.HttpException -> {
                UiError.HttpError(resourceHelper.getFormattedString(R.string.error_http_message_format, arrayOf(exception.errorCode)), exception.message)
            }
            else -> {
                UiError.GeneralUiError(exception.message ?: resourceHelper.getString(R.string.error_unknown_message))
            }
        }
    }

}