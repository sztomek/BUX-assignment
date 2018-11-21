package hu.sztomek.buxassignment.presentation.screen.details

import android.content.Context
import android.content.Intent
import android.widget.Toast
import hu.sztomek.buxassignment.R
import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.model.ISelectableProduct
import hu.sztomek.buxassignment.presentation.common.BaseActivity
import hu.sztomek.buxassignment.presentation.common.UiState
import hu.sztomek.buxassignment.presentation.model.ProductDetailsModel
import kotlinx.android.synthetic.main.activity_product_details.*
import timber.log.Timber

class ProductDetailsActivity : BaseActivity<ProductDetailsModel, ProductDetailsViewModel>() {

    companion object {
        private const val KEY_PRODUCT_ID = "productId"

        fun starter(context: Context, product: ISelectableProduct): Intent {
            return Intent(context, ProductDetailsActivity::class.java)
                .apply { putExtra(KEY_PRODUCT_ID, product.identifier) }
        }

        private fun getProductIdFromIntent(intent: Intent?): String? {
            return intent?.getStringExtra(KEY_PRODUCT_ID)
        }
    }

    override fun initUi() {
        setContentView(R.layout.activity_product_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.sendAction(Action.GetProductDetails(getProductIdFromIntent(intent) ?: throw IllegalArgumentException("Missing product id!")))
    }

    override fun getDefaultInitialState(): ProductDetailsModel {
        return ProductDetailsModel(getProductIdFromIntent(intent) ?: throw IllegalArgumentException("Missing product id!"))
    }

    override fun render(it: UiState?) {
        it?.let {
            when (it) {
                is UiState.LoadingState -> {
                    // TODO display loading indicator
                    Timber.d("loading...")
                }
                is UiState.ErrorState -> {
                    // TODO display error message
                    Toast.makeText(this@ProductDetailsActivity, it.uiError.message, Toast.LENGTH_SHORT).show()
                }
                is UiState.IdleState -> {
                    val data = it.data as? ProductDetailsModel ?: throw IllegalArgumentException("fail")
                    data.model?.let {
                        productdetails_symbol.text = it.symbol
                        productdetails_securityid.text = it.identifier
                        productdetails_displayname.text = it.displayName
                        productdetails_currentprice.text = "${it.currentPrice.amount} ${it.currentPrice.currency}"
                        productdetails_closingprice.text = "${it.closingPrice.amount} ${it.closingPrice.currency}"
                        productdetails_difference.text = "999%"

                        supportActionBar?.title = it.displayName
                    }
                }
            }
        }
    }
}