package hu.sztomek.buxassignment.presentation.common.adapter

import android.widget.BaseAdapter

abstract class BaseSpinnerAdapter<T> : BaseAdapter() {

    private val data = mutableListOf<T>()

    override fun getItem(position: Int): T {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.size
    }

    fun setData(data: List<T>) {
        this.data.clear()
        this.data.addAll(data)

        notifyDataSetChanged()
    }

}