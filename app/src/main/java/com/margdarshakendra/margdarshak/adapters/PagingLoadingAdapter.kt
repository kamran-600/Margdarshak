package com.margdarshakendra.margdarshak.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.margdarshakendra.margdarshak.databinding.PagingLoaderLayoutBinding

class PagingLoadingAdapter : LoadStateAdapter<PagingLoadingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        val binding = PagingLoaderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.binding.root.isVisible = loadState is LoadState.Loading
    }

    class ViewHolder(val binding : PagingLoaderLayoutBinding) : RecyclerView.ViewHolder(binding.root)

}