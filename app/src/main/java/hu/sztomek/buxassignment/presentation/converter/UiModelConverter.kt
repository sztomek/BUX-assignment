package hu.sztomek.buxassignment.presentation.converter

import hu.sztomek.buxassignment.domain.model.SelectableProducts
import hu.sztomek.buxassignment.presentation.model.ProductSelectModel

fun SelectableProducts.toUiModel() = ProductSelectModel(null, products)