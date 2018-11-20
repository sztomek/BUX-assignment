package hu.sztomek.buxassignment.domain.resource

import androidx.annotation.StringRes

interface ResourceHelper {

    fun getString(@StringRes resourceId: Int): String
    fun getFormattedString(@StringRes resourceId: Int, formatArgs: Array<Any?>): String

}