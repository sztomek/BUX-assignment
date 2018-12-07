package hu.sztomek.buxassignment.presentation.navigation

import androidx.appcompat.app.AppCompatActivity
import hu.sztomek.buxassignment.domain.model.ISelectableProduct
import hu.sztomek.buxassignment.presentation.screen.details.ProductDetailsActivity

class NavigatorImpl(private var activity: AppCompatActivity? = null) : Navigator {

    override fun takeActivity(activity: AppCompatActivity) {
        this.activity = activity
    }

    override fun goBack() {
        activity?.onBackPressed()
    }

    override fun showDetails(selectedProduct: ISelectableProduct) {
        activity?.let {
            it.startActivity(ProductDetailsActivity.starter(it, selectedProduct))
        }
    }
}