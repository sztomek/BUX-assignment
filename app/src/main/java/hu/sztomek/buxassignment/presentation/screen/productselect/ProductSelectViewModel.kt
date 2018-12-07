package hu.sztomek.buxassignment.presentation.screen.productselect

import hu.sztomek.buxassignment.R
import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.error.DomainException
import hu.sztomek.buxassignment.domain.interactor.GetSelectableProductsInteractor
import hu.sztomek.buxassignment.domain.model.NoOpDomainModel
import hu.sztomek.buxassignment.domain.model.SelectableProducts
import hu.sztomek.buxassignment.domain.operation.Operation
import hu.sztomek.buxassignment.domain.resource.ResourceHelper
import hu.sztomek.buxassignment.domain.scheduler.WorkSchedulers
import hu.sztomek.buxassignment.presentation.common.BaseViewModel
import hu.sztomek.buxassignment.presentation.common.UiError
import hu.sztomek.buxassignment.presentation.common.UiState
import hu.sztomek.buxassignment.presentation.converter.toUiModel
import hu.sztomek.buxassignment.presentation.model.ProductSelectModel
import io.reactivex.Flowable
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
            Flowable.merge(
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
                    },
                upstream.ofType(Action.SelectProduct::class.java)
                    .flatMap { action -> Flowable.just(Operation.Completed(action, NoOpDomainModel)) }
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
                        UiError.GeneralUiError(resourceHelper.getString(R.string.error_unknown_message)),
                        oldState.data
                    )
                }
                is Operation.Completed -> {
                    UiState.IdleState(data =
                    if (operation.action is Action.SelectProduct) (oldState.data as ProductSelectModel).copy(selectedProductId = (operation.action as Action.SelectProduct).selectedProduct.identifier)
                    else (operation.result as SelectableProducts).toUiModel())
                }
            }
        }
    }
}