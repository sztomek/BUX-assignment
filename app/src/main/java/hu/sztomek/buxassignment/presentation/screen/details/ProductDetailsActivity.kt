package hu.sztomek.buxassignment.presentation.screen.details

import android.content.Context
import android.content.Intent
import hu.sztomek.buxassignment.R
import hu.sztomek.buxassignment.R.id.*
import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.model.ISelectableProduct
import hu.sztomek.buxassignment.domain.resource.ResourceHelper
import hu.sztomek.buxassignment.presentation.common.BaseActivity
import hu.sztomek.buxassignment.presentation.common.BaseViewModel
import hu.sztomek.buxassignment.presentation.common.UiError
import hu.sztomek.buxassignment.presentation.common.UiState
import hu.sztomek.buxassignment.presentation.common.dialog.ErrorDialogClickListener
import hu.sztomek.buxassignment.presentation.common.dialog.ErrorDialogFragment
import hu.sztomek.buxassignment.presentation.common.dialog.LoadingDialogFragment
import hu.sztomek.buxassignment.presentation.converter.differenceInPercentTo
import hu.sztomek.buxassignment.presentation.converter.toFormattedDate
import hu.sztomek.buxassignment.presentation.converter.toFormattedString
import hu.sztomek.buxassignment.presentation.converter.updatesButtonLabel
import hu.sztomek.buxassignment.presentation.model.ProductDetailsModel
import kotlinx.android.synthetic.main.activity_product_details.*
import javax.inject.Inject

class ProductDetailsActivity : BaseActivity<ProductDetailsModel>() {

    companion object {
        private const val KEY_PRODUCT_ID = "productId"
        private const val TAG_DIALOG = "shown_dialog"

        fun starter(context: Context, product: ISelectableProduct): Intent {
            return Intent(context, ProductDetailsActivity::class.java)
                .apply { putExtra(KEY_PRODUCT_ID, product.identifier) }
        }

        private fun getProductIdFromIntent(intent: Intent?): String? {
            return intent?.getStringExtra(KEY_PRODUCT_ID)
        }
    }

    @Inject lateinit var resourceHelper: ResourceHelper

    private val productId by lazy {
        getProductIdFromIntent(intent) ?: throw IllegalArgumentException("Missing product id!")
    }

    private val restErrorClickListener: ErrorDialogClickListener by lazy {
        object: ErrorDialogClickListener {
            override fun onButtonClicked(which: ErrorDialogFragment.ErrorDialogButtons) {
                when(which) {
                    ErrorDialogFragment.ErrorDialogButtons.NEGATIVE -> finish()
                    ErrorDialogFragment.ErrorDialogButtons.POSITIVE -> viewModel.sendAction(Action.GetProductDetails(productId))
                    else -> {
                        // ignored
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.sendAction(Action.GetProductDetails(productId))

        viewModel.stateStream.value?.let {
            val productDetailsModel = it.data as ProductDetailsModel
            if (productDetailsModel.liveUpdateEnabled) {
                viewModel.sendAction(Action.UpdateSubscriptions(listOf(productDetailsModel.model!!), emptyList()))
            }
        }

        viewModel.sendAction(Action.GetSocketErrors)
    }

    override fun onStop() {
        viewModel.sendAction(Action.StopUpdates)

        super.onStop()
    }

    override fun getViewModelClass(): Class<out BaseViewModel> {
        return ProductDetailsViewModel::class.java
    }

    override fun initUi() {
        setContentView(R.layout.activity_product_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        productdetails_live_updates_button.setOnClickListener {
            (viewModel.stateStream.value?.data as? ProductDetailsModel)?.let {
                if (it.liveUpdateEnabled) {
                    viewModel.sendAction(Action.UpdateSubscriptions(emptyList(), listOf(it.model!!)))
                } else {
                    viewModel.sendAction(Action.UpdateSubscriptions(listOf(it.model!!), emptyList()))
                }
            }
        }
    }

    override fun getDefaultInitialState(): ProductDetailsModel {
        return ProductDetailsModel(productId)
    }

    override fun render(it: UiState?) {
        it?.let {
            when (it) {
                is UiState.LoadingState -> {
                    hideErrorDialog()
                    showLoading()
                }
                is UiState.ErrorState -> {
                    hideLoading()

                    if (it.uiError is UiError.SocketClosedError) {
                        productdetails_live_updates_button.text = resourceHelper.getString((it.data as ProductDetailsModel).updatesButtonLabel())
                    }

                    showErrorDialog(it.uiError)
                }
                is UiState.IdleState -> {
                    hideErrorDialog()
                    hideLoading()

                    val data = it.data as? ProductDetailsModel ?: throw IllegalArgumentException("Failed to cast model data!")
                    data.model?.let {
                        productdetails_symbol.text = it.symbol
                        productdetails_securityid.text = it.identifier
                        productdetails_displayname.text = it.displayName
                        productdetails_currentprice.text = it.currentPrice.toFormattedString()
                        productdetails_closingprice.text = it.closingPrice.toFormattedString()
                        productdetails_difference.text = it.currentPrice.differenceInPercentTo(it.closingPrice)
                        productdetails_last_update.text = data.toFormattedDate()
                        productdetails_live_updates_button.text = resourceHelper.getString(data.updatesButtonLabel())

                        supportActionBar?.title = it.displayName
                    }
                }
            }
        }
    }

    private inline fun <reified T> findFragmentByTag(tag: String): T? {
        return supportFragmentManager.findFragmentByTag(tag) as? T
    }

    private fun showLoading() {
        LoadingDialogFragment.create().show(supportFragmentManager, TAG_DIALOG)
    }

    private fun hideLoading() {
        findFragmentByTag<LoadingDialogFragment>(TAG_DIALOG)?.apply {
            dismiss()
        }
    }

    private fun showErrorDialog(uiError: UiError) {
        when (uiError) {
            is UiError.HttpError, is UiError.RestCommunicationError -> {
                showRestErrorDialog(uiError.message)
            }
            is UiError.SocketClosedError -> {
                showSocketErrorDialog(uiError.message)
            }
        }

    }

    private fun showRestErrorDialog(message: String) {
        val buttons = mapOf(
            ErrorDialogFragment.ErrorDialogButtons.POSITIVE to resourceHelper.getString(R.string.retry),
            ErrorDialogFragment.ErrorDialogButtons.NEGATIVE to resourceHelper.getString(R.string.go_back)
        )

        ErrorDialogFragment.create(
            ErrorDialogFragment.ErrorDialogModel(
                resourceHelper.getString(R.string.alert_title),
                message,
                buttons),
            restErrorClickListener)
            .show(supportFragmentManager, TAG_DIALOG)
    }

    private fun showSocketErrorDialog(message: String) {
        val buttons = mapOf(
            ErrorDialogFragment.ErrorDialogButtons.NEUTRAL to resourceHelper.getString(android.R.string.ok)
        )

        ErrorDialogFragment.create(
            ErrorDialogFragment.ErrorDialogModel(
                resourceHelper.getString(R.string.alert_title),
                message,
                buttons
            ),
            object: ErrorDialogClickListener {
                override fun onButtonClicked(which: ErrorDialogFragment.ErrorDialogButtons) {
                    // no-op
                }

            }
        ).show(supportFragmentManager, TAG_DIALOG)
    }

    private fun hideErrorDialog() {
        findFragmentByTag<ErrorDialogFragment>(TAG_DIALOG)?.apply {
            dismiss()
        }
    }

}