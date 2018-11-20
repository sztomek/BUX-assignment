package hu.sztomek.buxassignment.presentation.common.adapter

import android.view.View

abstract class BaseViewHolder<T> protected constructor(val view: View) {

    abstract fun bind(data: T)

}
