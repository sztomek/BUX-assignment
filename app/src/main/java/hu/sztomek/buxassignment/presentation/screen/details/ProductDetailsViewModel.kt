package hu.sztomek.buxassignment.presentation.screen.details

import hu.sztomek.buxassignment.R
import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.error.DomainException
import hu.sztomek.buxassignment.domain.interactor.GetProductDetailsInteractor
import hu.sztomek.buxassignment.domain.model.ProductDetails
import hu.sztomek.buxassignment.domain.operation.Operation
import hu.sztomek.buxassignment.domain.resource.ResourceHelper
import hu.sztomek.buxassignment.domain.scheduler.WorkSchedulers
import hu.sztomek.buxassignment.presentation.common.BaseViewModel
import hu.sztomek.buxassignment.presentation.common.UiError
import hu.sztomek.buxassignment.presentation.common.UiState
import hu.sztomek.buxassignment.presentation.converter.toUiModel
import io.reactivex.FlowableTransformer
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class ProductDetailsViewModel @Inject constructor(
    workSchedulers: WorkSchedulers,
    private val resourceHelper: ResourceHelper,
    private val productDetailsInteractor: GetProductDetailsInteractor
) : BaseViewModel(workSchedulers) {

    override fun invokeActions(): FlowableTransformer<Action, Operation> {
        return FlowableTransformer { upstream ->
            upstream
                .ofType(Action.GetProductDetails::class.java)
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
                }
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
                    val productDetails = operation.result as ProductDetails
                    UiState.IdleState(productDetails.toUiModel())
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