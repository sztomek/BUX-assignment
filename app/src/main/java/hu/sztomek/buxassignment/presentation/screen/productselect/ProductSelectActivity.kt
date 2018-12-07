package hu.sztomek.buxassignment.presentation.screen.productselect

import android.view.View
import android.widget.AdapterView
import hu.sztomek.buxassignment.R
import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.model.ISelectableProduct
import hu.sztomek.buxassignment.presentation.common.BaseActivity
import hu.sztomek.buxassignment.presentation.common.BaseViewModel
import hu.sztomek.buxassignment.presentation.common.UiState
import hu.sztomek.buxassignment.presentation.model.ProductSelectModel
import hu.sztomek.buxassignment.presentation.screen.productselect.adapter.SelectProductSpinnerAdapter
import kotlinx.android.synthetic.main.activity_product_select.*
import timber.log.Timber

class ProductSelectActivity : BaseActivity<ProductSelectModel>() {

    private val selectProductSpinnerAdapter = SelectProductSpinnerAdapter()

    override fun getViewModelClass(): Class<out BaseViewModel> {
        return ProductSelectViewModel::class.java
    }

    override fun initUi() {
        setContentView(R.layout.activity_product_select)

        productselect_spinner.adapter = selectProductSpinnerAdapter
        productselect_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Timber.d("onNothingSelected")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Timber.d("onItemSelected: position [$position], id [$id]")

                viewModel.sendAction(Action.SelectProduct(selectProductSpinnerAdapter.getItem(position)))
            }
        }

        productselect_button.setOnClickListener {
            navigator.showDetails(productselect_spinner.selectedItem as ISelectableProduct)
        }

        viewModel.sendAction(Action.GetSelectableProducts)
    }

    override fun getDefaultInitialState(): ProductSelectModel {
        return ProductSelectModel(null)
    }

    override fun render(it: UiState?) {
        it.let {
            when(it) {
                is UiState.IdleState -> {
                    val productSelectModel = it.data as? ProductSelectModel

                    if (productSelectModel == null) {
                        Timber.e("UiState.IdleState has invalid data: [$productSelectModel]")
                    } else {
                        selectProductSpinnerAdapter.setData(productSelectModel.selectableProducts)
                        val selectedItem = productSelectModel.selectableProducts
                            .singleOrNull { it.identifier == productSelectModel.selectedProductId }

                        if (selectedItem != null) {
                            productselect_spinner.setSelection(selectProductSpinnerAdapter.getPositionOf(selectedItem))
                        }
                    }
                }
                else -> {
                    Timber.d("Unhandled state: [$it]")
                }
            }
        }
    }

}