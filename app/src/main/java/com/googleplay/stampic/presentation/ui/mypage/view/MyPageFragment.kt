package com.googleplay.stampic.presentation.ui.mypage.view

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.googleplay.stampic.R
import com.googleplay.stampic.data.remote.model.home.ResponseAttrData
import com.googleplay.stampic.databinding.FragmentMyPageBinding
import com.googleplay.stampic.presentation.ui.base.BaseFragment
import com.googleplay.stampic.presentation.ui.home.adapter.HomeListRecyclerViewAdapter
import com.googleplay.stampic.presentation.ui.home.viewmodel.HomeViewModel
import com.googleplay.stampic.presentation.ui.mypage.viewmodel.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    internal val viewModel: MyPageViewModel by viewModels()
    private lateinit var homeListViewAdapter: HomeListRecyclerViewAdapter
    private val homeViewModel: HomeViewModel by viewModels()
    private var selectedGugunNm: String? = null

    companion object {
        const val USER_NAME = "user_name"
        const val USER_EMAIL = "user_email"
        const val USER_IMAGE = "user_image"
        const val USER_NICKNAME = "user_nickname"
        const val STAMP_COUNT = "stamp_count"
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyInfo()
    }

    override fun initStartView() {
        binding.ivMyImage.clipToOutline = true

        homeListViewAdapter =
            HomeListRecyclerViewAdapter()
        binding.activityGroup.rvHome.apply {
            adapter = homeListViewAdapter
            itemAnimator = null
        }

        binding.activityGroup.layoutSwipeRefresh.setOnRefreshListener {
            Handler(Looper.getMainLooper())
                .postDelayed({
                    binding.activityGroup.layoutSwipeRefresh.isRefreshing = false
                }, 1000)
        }

        // myPageFragment를 설정
        homeListViewAdapter.setMyPageFragment(this)

        homeListViewAdapter.apply {
            homeViewModel.getAttrList()
            Log.d("dd","getAttrList() 실행")
        }

        // 스피너 선택
        binding.spProvince.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedGugunNm = resources.getStringArray(R.array.gugun_nm_list)[position]
                Log.d("dd","selectedGugunNm의 값:"+selectedGugunNm.toString())
                homeListViewAdapter.filterByGugunNm(selectedGugunNm)
                homeViewModel.attrList.observe(viewLifecycleOwner) { attrList ->
                    val filteredList = attrList.filter { attrInfo ->
                        attrInfo.gugunNm == selectedGugunNm
                    }
                    homeListViewAdapter.submitList(filteredList)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    override fun initDataBinding() {
//        homeViewModel.attrList.observe(viewLifecycleOwner) { attrList ->
//            homeListViewAdapter.submitList(attrList)
//        }

        viewModel.myInfoData.observe(viewLifecycleOwner) { myInfo ->
            Log.d("dd","닉네임은 넘어왔나요?"+myInfo.nickName)
            Log.d("dd","attrList는 넘어올까요?"+myInfo.stampAttrList.toString())
            binding.tvMyName.text = myInfo.nickName
            binding.tvStampCount.text = myInfo.stampCount
        }
    }

    override fun initAfterBinding() {
    }
}