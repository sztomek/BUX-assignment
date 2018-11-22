package hu.sztomek.buxassignment.presentation.common.dialog

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

abstract class BaseDialogFragment : DialogFragment() {

    override fun show(manager: FragmentManager?, tag: String?) {
        manager?.let {
            val transaction = it.beginTransaction()
            val previous = it.findFragmentByTag(tag)
            if (previous != null) {
                transaction.remove(previous)
            }

            super.show(transaction, tag)
            manager.executePendingTransactions()
        }
    }

}