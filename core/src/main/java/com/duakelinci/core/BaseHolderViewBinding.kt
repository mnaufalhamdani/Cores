package com.duakelinci.core

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseHolder<M>(itemBinding: ViewBinding)
    : RecyclerView.ViewHolder(itemBinding.root)
{
    abstract fun bind(position:Int, model:M)
}