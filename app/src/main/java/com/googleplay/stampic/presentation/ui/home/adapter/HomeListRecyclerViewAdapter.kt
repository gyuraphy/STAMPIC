package com.googleplay.stampic.presentation.ui.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.googleplay.stampic.data.remote.model.home.ResponseAttrData
import com.googleplay.stampic.databinding.ItemAttrListBinding
import com.googleplay.stampic.presentation.ui.home.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeListRecyclerViewAdapter (

) : ListAdapter<ResponseAttrData.Data.AttrInfo, HomeListRecyclerViewAdapter.ViewHolder>(diffUtil) {

    private lateinit var binding: ItemAttrListBinding
    var attrList = mutableListOf<ResponseAttrData.Data.AttrInfo>()

    inner class ViewHolder(private  val binding: ItemAttrListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseAttrData.Data.AttrInfo) {
            binding.ivAttrImage.clipToOutline = true
            Glide.with(binding.ivAttrImage.context).load(item.thumb)
                .into(binding.ivAttrImage)
            binding.tvAttrName.text = item.place
            binding.tvAttrIntroduce.text = item.contents
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemAttrListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun insertList(item: MutableList<ResponseAttrData.Data.AttrInfo>) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("dd",item.toString())
                attrList.addAll(item)
                withContext(Dispatchers.Main) {
                    notifyDataSetChanged()
                }
        }
    }

    companion object {

        private val diffUtil = object : DiffUtil.ItemCallback<ResponseAttrData.Data.AttrInfo>() {
            override fun areItemsTheSame(
                oldItem: ResponseAttrData.Data.AttrInfo,
                newItem: ResponseAttrData.Data.AttrInfo,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ResponseAttrData.Data.AttrInfo,
                newItem: ResponseAttrData.Data.AttrInfo,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}