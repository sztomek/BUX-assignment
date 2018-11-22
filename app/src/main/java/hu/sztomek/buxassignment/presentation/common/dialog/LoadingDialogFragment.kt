package hu.sztomek.buxassignment.presentation.common.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import hu.sztomek.buxassignment.R

class LoadingDialogFragment : BaseDialogFragment() {

    companion object {
        private const val ARG_MESSAGE = "loadingdialog_message"

        fun create(message: String? = null): LoadingDialogFragment {
            val loadingDialogFragment = LoadingDialogFragment()
            Bundle().apply {
                putString(ARG_MESSAGE, message)
            }.apply { loadingDialogFragment.arguments = this }

            return loadingDialogFragment
        }

        private fun getMessageFromArgs(arguments: Bundle?): String? {
            return arguments?.getString(ARG_MESSAGE)
        }
    }

    private val message: String by lazy {
        getMessageFromArgs(arguments) ?: getString(R.string.loading_message)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.LoadingDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isCancelable = false

        return inflater.inflate(R.layout.layout_loading_indicator, container)
            .apply { findViewById<TextView>(R.id.loading_indicator_message).text = message }
    }
}