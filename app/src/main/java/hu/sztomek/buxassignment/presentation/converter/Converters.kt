package hu.sztomek.buxassignment.presentation.converter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import hu.sztomek.buxassignment.domain.model.Price
import hu.sztomek.buxassignment.domain.model.ProductDetails
import hu.sztomek.buxassignment.domain.model.SelectableProducts
import hu.sztomek.buxassignment.presentation.model.ProductDetailsModel
import hu.sztomek.buxassignment.presentation.model.ProductSelectModel
import timber.log.Timber
import java.text.NumberFormat
import java.util.*

fun SelectableProducts.toUiModel() = ProductSelectModel(null, products)

fun ProductDetails.toUiModel() = ProductDetailsModel(identifier, this)

fun Price.toFormattedString(): String {
    return try {
        val formatter = NumberFormat.getCurrencyInstance()
        formatter.currency = Currency.getInstance(currency)
        formatter.maximumFractionDigits = decimals

        formatter.format(amount.toDouble())
    } catch (e: Exception) {
        Timber.e("Failed to convert Price [$this] to formatted string: [$e]")

        "$currency $amount"
    }
}

fun Price.differenceInPercentTo(other: Price): SpannableString {
    return try {
        val percent = amount.toDouble() / other.amount.toDouble() * 100
        val difference = percent - 100
        val formattedDifference = "%.2f".format(percent - 100)
        val spannableString = SpannableString("$formattedDifference %")
        spannableString.setSpan(ForegroundColorSpan(if (difference > 0) Color.GREEN else Color.RED), 0, spannableString.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        spannableString
    } catch (e: Exception) {
        SpannableString("N/A")
    }
}