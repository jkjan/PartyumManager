package com.partyum.partyummanager.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T: ViewDataBinding> : RecyclerView.Adapter<BaseRecyclerViewAdapter<T>.Holder>(){
    lateinit var binding: T
    abstract val layoutResourceId: Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater, layoutResourceId, parent, false)
        return Holder(binding)
    }

    abstract override fun getItemCount(): Int

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(position)
    }

    abstract fun setView(position: Int)

    open inner class Holder(val binding: T): RecyclerView.ViewHolder(binding.root) {
        open fun bind(position: Int) {
            setView(position)
        }
    }
}