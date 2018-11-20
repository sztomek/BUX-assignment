package hu.sztomek.buxassignment.presentation.screen.productselect

import hu.sztomek.buxassignment.R
import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.error.DomainException
import hu.sztomek.buxassignment.domain.interactor.GetSelectableProductsInteractor
import hu.sztomek.buxassignment.domain.model.SelectableProducts
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

class ProductSelectViewModel @Inject constructor(
    private val selectableProductsUseCase: GetSelectableProductsInteractor,
    private val resourceHelper: ResourceHelper,
    workSchedulers: WorkSchedulers
) : BaseViewModel(workSchedulers) {

    override fun invokeActions(): FlowableTransformer<Action, Operation> {
        return FlowableTransformer { upstream ->
            upstream.ofType(Action.GetSelectableProducts::class.java)
                .flatMap { action ->
                    selectableProductsUseCase.execute(action)
                        .map { Operation.Completed(action, it) as Operation }
                        .startWith(Operation.InProgress(action))
                        .onErrorReturn { t ->
                            Operation.Failed(
                                action,
                                DomainException.GeneralDomainException(
                                    t.message ?: resourceHelper.getString(R.string.error_unknown_message)
                                )
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
                        UiError.GeneralUiError(resourceHelper.getString(R.string.error_unknown_message)),
                        oldState.data
                    )
                }
                is Operation.Completed -> {
                    UiState.IdleState((operation.result as SelectableProducts).toUiModel())
                }
            }
        }
    }
}