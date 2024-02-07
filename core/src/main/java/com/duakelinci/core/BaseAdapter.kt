package com.duakelinci.core

import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<Model, Holder : BaseHolder<Model>>(
    private val list: List<Model>
) : RecyclerView.Adapter<Holder>() {
    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(position, list[position])
    }
}