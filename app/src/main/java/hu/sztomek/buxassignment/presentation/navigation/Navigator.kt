package hu.sztomek.buxassignment.presentation.navigation

import androidx.appcompat.app.AppCompatActivity
import hu.sztomek.buxassignment.domain.model.ISelectableProduct

interface Navigator {

    fun takeActivity(activity: AppCompatActivity)

    fun goBack()
    fun showDetails(selectedProduct: ISelectableProduct)

}