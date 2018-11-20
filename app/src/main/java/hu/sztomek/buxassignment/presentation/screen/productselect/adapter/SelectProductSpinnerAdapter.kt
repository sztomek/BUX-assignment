package hu.sztomek.buxassignment.presentation.screen.productselect.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import hu.sztomek.buxassignment.domain.model.ISelectableProduct
import hu.sztomek.buxassignment.presentation.common.adapter.BaseSpinnerAdapter
import hu.sztomek.buxassignment.presentation.common.adapter.BaseViewHolder

class SelectProductSpinnerAdapter : BaseSpinnerAdapter<ISelectableProduct>() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        if (convertView == null) {
            val viewHolder = ViewHolder.create(parent)
            viewHolder.bind(getItem(position))
            val view = viewHolder.view
            view.tag = viewHolder

            return view
        }

        (convertView.tag as? ViewHolder)?.bind(getItem(position))

        return convertView
    }

    private class ViewHolder private constructor(view: View): BaseViewHolder<ISelectableProduct>(view) {

        companion object {
            fun create(parent: ViewGroup?): ViewHolder {
                return ViewHolder(LayoutInflater.from(parent?.context).inflate(android.R.layout.simple_selectable_list_item, parent, false))
            }
        }

        override fun bind(data: ISelectableProduct) {
            view.findViewById<TextView>(android.R.id.text1).text = data.displayName
        }
    }

}