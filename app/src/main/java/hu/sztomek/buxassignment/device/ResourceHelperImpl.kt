package hu.sztomek.buxassignment.device

import android.content.res.Resources
import hu.sztomek.buxassignment.domain.resource.ResourceHelper
import java.lang.IllegalArgumentException
import javax.inject.Inject

class ResourceHelperImpl @Inject constructor(private val resources: Resources) : ResourceHelper {

    override fun getString(resourceId: Int): String = resources.getString(resourceId)
        ?: throw IllegalArgumentException("Failed to find resource with id [$resourceId]")

    override fun getFormattedString(resourceId: Int, formatArgs: Array<Any?>): String = resources.getString(resourceId, formatArgs)
        ?: throw IllegalArgumentException("Failed to find resource with id [$resourceId]")
}