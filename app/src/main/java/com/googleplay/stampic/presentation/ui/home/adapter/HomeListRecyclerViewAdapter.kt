package com.googleplay.stampic.presentation.ui.home.adapter

import android.content.ClipData
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.googleplay.stampic.R
import com.googleplay.stampic.data.remote.model.home.ResponseAttrData
import com.googleplay.stampic.databinding.ItemAttrListBinding
import com.googleplay.stampic.presentation.ui.mypage.view.MyPageFragment


class HomeListRecyclerViewAdapter :
    ListAdapter<ResponseAttrData.Data.AttrInfo, HomeListRecyclerViewAdapter.ViewHolder>(diffUtil) {

    private var onItemClickListener: ((ResponseAttrData.Data.AttrInfo) -> Unit)? = null
    private var dataList: List<ResponseAttrData.Data.AttrInfo> = emptyList()
    private lateinit var binding: ItemAttrListBinding
    var attrList = mutableListOf<ResponseAttrData.Data.AttrInfo>()

    private var myPageFragment: MyPageFragment? = null

    inner class ViewHolder(private  val binding: ItemAttrListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseAttrData.Data.AttrInfo) {
            binding.ivAttrImage.clipToOutline = true
            Glide.with(binding.ivAttrImage.context).load(item.thumb)
                .into(binding.ivAttrImage)
            binding.tvAttrName.text = item.place
            binding.tvAttrIntroduce.text = item.contents

            myPageFragment?.let { fragment ->
                val stampAttrList = fragment.viewModel.myInfoData.value?.stampAttrList ?: emptyList()
                val attrId = item.id

                if (stampAttrList.contains(attrId)) {
                    binding.ivHidden.visibility = View.VISIBLE
                    binding.ivAttrImage.foreground = ContextCompat.getDrawable(binding.root.context, R.drawable.black_transparent_layer)
                } else {
                    binding.ivHidden.visibility = View.GONE
                    binding.ivAttrImage.foreground = null
                }
            }

            binding.root.setOnClickListener {
                onItemClickListener?.invoke(item)
            }
        }
    }

    fun setMyPageFragment(fragment: MyPageFragment) {
        myPageFragment = fragment
    }
    fun setOnItemClickListener(listener: (ResponseAttrData.Data.AttrInfo) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAttrListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
    fun filterByGugunNm(gugunNm: String) {
        if (attrList.isEmpty()) {
            // attrList가 비어있는 경우 처리할 로직 추가
            Log.d("dd", "attrList is empty")
        } else {
            // attrList에 데이터가 존재하는 경우 처리할 로직 추가
            Log.d("dd", "attrList is not empty")
        }
        val filteredList = attrList.filter { attrInfo ->
            attrInfo.gugunNm == gugunNm
        }.toMutableList()
        Log.d("dd","gugunNm에 따른 리스트 맞니?"+filteredList.toString())
        submitList(filteredList)
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<ResponseAttrData.Data.AttrInfo>() {
            override fun areItemsTheSame(
                oldItem: ResponseAttrData.Data.AttrInfo,
                newItem: ResponseAttrData.Data.AttrInfo,
            ): Boolean {
                return oldItem.id == newItem.id
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