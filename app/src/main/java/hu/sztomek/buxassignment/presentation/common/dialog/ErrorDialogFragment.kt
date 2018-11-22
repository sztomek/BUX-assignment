package hu.sztomek.buxassignment.presentation.common.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.parcel.Parcelize

interface ErrorDialogClickListener {
    fun onButtonClicked(which: ErrorDialogFragment.ErrorDialogButtons)
}

class ErrorDialogFragment : BaseDialogFragment() {

    companion object {

        private const val ARG_MODEL = "alertdialog_model"

        fun create(model: ErrorDialogModel, clickListener: ErrorDialogClickListener): ErrorDialogFragment {
            val errorDialogFragment = ErrorDialogFragment()
            return errorDialogFragment.apply {
                this.clickListener = clickListener
                arguments = Bundle().apply {
                    putParcelable(ARG_MODEL, model)
                }
            }
        }

        fun getModelFromArguments(arguments: Bundle?): ErrorDialogModel? {
            return arguments?.getParcelable(ARG_MODEL) as? ErrorDialogModel
        }
    }

    private val model: ErrorDialogModel by lazy {
        getModelFromArguments(arguments) ?: throw IllegalStateException("Failed to read model from arguments!")
    }

    private lateinit var clickListener: ErrorDialogClickListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        val builder = AlertDialog.Builder(activity)
            .setTitle(model.title)
            .setMessage(model.message)
        model.buttonLabels.forEach {
            when (it.key) {
                ErrorDialogButtons.NEUTRAL -> builder.setNeutralButton(it.value) { _, _ -> clickListener.onButtonClicked(ErrorDialogButtons.NEUTRAL) }
                ErrorDialogButtons.NEGATIVE -> builder.setNegativeButton(it.value) { _, _ -> clickListener.onButtonClicked(ErrorDialogButtons.NEGATIVE)}
                ErrorDialogButtons.POSITIVE -> builder.setPositiveButton(it.value) { _, _ -> clickListener.onButtonClicked(ErrorDialogButtons.POSITIVE)}
            }
        }

        return builder.create()
    }

    enum class ErrorDialogButtons {
        NEUTRAL,
        POSITIVE,
        NEGATIVE
    }

    @Parcelize
    data class ErrorDialogModel(val title: String,
                                val message: String,
                                val buttonLabels: Map<ErrorDialogButtons, String> = emptyMap()
    ) : Parcelable

}