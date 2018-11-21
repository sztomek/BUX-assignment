package hu.sztomek.buxassignment.presentation.screen.productselect

import android.view.View
import android.widget.AdapterView
import hu.sztomek.buxassignment.R
import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.model.ISelectableProduct
import hu.sztomek.buxassignment.presentation.common.BaseActivity
import hu.sztomek.buxassignment.presentation.common.UiState
import hu.sztomek.buxassignment.presentation.model.ProductSelectModel
import hu.sztomek.buxassignment.presentation.screen.details.ProductDetailsActivity
import hu.sztomek.buxassignment.presentation.screen.productselect.adapter.SelectProductSpinnerAdapter
import kotlinx.android.synthetic.main.activity_product_select.*
import timber.log.Timber

class ProductSelectActivity : BaseActivity<ProductSelectModel, ProductSelectViewModel>() {

    private val selectProductSpinnerAdapter = SelectProductSpinnerAdapter()

    override fun initUi() {
        setContentView(R.layout.activity_product_select)

        productselect_spinner.adapter = selectProductSpinnerAdapter
        productselect_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Timber.d("onNothingSelected")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Timber.d("onItemSelected: position [$position], id [$id]")

                // TODO action to set selection and create new state
            }
        }

        productselect_button.setOnClickListener {
            startActivity(ProductDetailsActivity.starter(this@ProductSelectActivity, productselect_spinner.selectedItem as ISelectableProduct))
        }
    }

    override fun onStart() {
        super.onStart()

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
                        productselect_spinner.setSelection(0) // TODO get position
                    }
                }
                else -> {
                    Timber.d("Unhandled state: [$it]")
                }
            }
        }
    }

}