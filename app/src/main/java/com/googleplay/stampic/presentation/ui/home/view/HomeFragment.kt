package com.googleplay.stampic.presentation.ui.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.googleplay.stampic.domain.model.DialogInfo
import com.googleplay.stampic.presentation.ui.base.BaseFragment
import com.googleplay.stampic.presentation.util.DialogFragmentUtil
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.googleplay.stampic.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import com.googleplay.stampic.R
import com.googleplay.stampic.presentation.ui.home.adapter.HomeListRecyclerViewAdapter
import com.googleplay.stampic.presentation.ui.home.viewmodel.HomeViewModel
import com.googleplay.stampic.data.remote.model.home.ResponseAttrData

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home), OnMapReadyCallback {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var homeListViewAdapter: HomeListRecyclerViewAdapter
    private lateinit var mContext: Context
    private lateinit var googleMap:GoogleMap


    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
//                getMyCurrentLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
//                getMyCurrentLocation()
            }
            else -> {
                // 권한 거부
                permissionDialog()
            }
        }
    }

    private val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        it.data?.getBooleanExtra("isModifySuccess",false).let { isModifySuccess ->
            if (isModifySuccess == true) {
                homeViewModel.getAttrList()
                binding.activityGroup.rvHome.scrollToPosition(0)
            }
        }
    }

    override fun initStartView() {
        Log.d("dd","initStartView 실행")
        binding.vm = homeViewModel


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

        homeListViewAdapter.apply {
            homeViewModel.getAttrList()
            Log.d("dd","getAttrList() 실행")
        }

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    //  로딩 다이얼로그를 표시하거나 숨기는 동작
    override fun initDataBinding() {
        Log.d("dd", "initDataBinding 실행")

        homeViewModel.attrList.observe(viewLifecycleOwner) {
            homeListViewAdapter.submitList(it)
            Log.d("dd", it.toString())
        }
    }

    // 데이터 바인딩 이후에 수행되는 초기화 작업
    override fun initAfterBinding() {
        Log.d("dd", "initAfterBinding 실행")

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.setOnItemReselectedListener {menuItem ->
            when (menuItem.itemId) {
                R.id.homeFragment -> {
                    binding.activityGroup.layoutSwipeRefresh.isRefreshing = true
                    binding.activityGroup.rvHome.scrollToPosition(0)
                    Handler(Looper.getMainLooper())
                        .postDelayed({
                            binding.activityGroup.layoutSwipeRefresh.isRefreshing = false
                        }, 1000)
                }
            }

        }
    }

    // 권한 거부 했을때
    private fun permissionDialog() {
        fun doPositiveClick() {
            startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context?.packageName, null)
                )
            )
        }

        val dialog = DialogFragmentUtil(
            DialogInfo(
                "위치 접근 권한",
                "위치 접근 권한이 필요합니다.\n확인을 누르면 설정화면으로 이동합니다.",
                "닫기",
                "확인"
            )
        ) { doPositiveClick() }
        dialog.show(requireActivity().supportFragmentManager, dialog.tag)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
    }

    // 지도

    private val sggNmList = mutableListOf<String>()
    private val polygonList = mutableListOf<Polygon>() // 폴리곤 리스트 추가

    private var clickedPolygon: Polygon? = null
    private var previousClickedSggNm: String? = null

    private val desiredSggNmList = listOf(
        "동래구", "남구", "영도구", "동구", "서구", "중구",
        "부산진구", "연제구", "강서구", "금정구", "사하구",
        "해운대구", "북구", "기장군", "사상구", "수영구"
    )

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // 시작위치 부산
        val busanLatLng = LatLng(35.1796, 129.0750)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(busanLatLng, 10f))

        googleMap.uiSettings.isZoomControlsEnabled = true

        // JSON file , simplebusan.json
        val jsonStr = loadJSONFromAsset()
        if (jsonStr != null) {
            try {
                val jsonObj = JSONObject(jsonStr)
                val features = jsonObj.getJSONArray("features")
                for (i in 0 until features.length()) {
                    val feature = features.getJSONObject(i)
                    val geometry = feature.getJSONObject("geometry")
                    val type = geometry.getString("type")
                    val coordinates = geometry.getJSONArray("coordinates")
                    val properties = feature.getJSONObject("properties")
                    val sggNm = properties.getString("SGG_NM")
                    sggNmList.add(sggNm)

                    if (type == "Polygon") {

                        val polygonOptions = drawPolygon(coordinates)
                        googleMap.addPolygon(polygonOptions)
                        val polygon = googleMap.addPolygon(polygonOptions)
                        polygon.tag = i // Set the index as a tag to retrieve SGG_NM later
                        polygonList.add(polygon)

                    } else if (type == "MultiPolygon") {

                        for (j in 0 until coordinates.length()) {
                            val polygonCoordinates = coordinates.getJSONArray(j)
                            val polygonOptions = drawPolygon(polygonCoordinates)
                            val polygon = googleMap.addPolygon(polygonOptions)
                            polygon.tag = i // Set the index as a tag to retrieve SGG_NM later
                            polygonList.add(polygon)
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        // polygon click listener
        googleMap.setOnPolygonClickListener { polygon ->

            // 폴리곤 색 변경
            clickedPolygon?.fillColor = Color.TRANSPARENT
            clickedPolygon = null

            // 선택된 폴리곤의 구군 가져오기
            val index  = polygon.tag as? Int
            val sggNm = sggNmList.getOrNull(index ?: -1)

            if (sggNm != null && desiredSggNmList.contains(sggNm)) {
                if (previousClickedSggNm != sggNm) {
                    // Clear previous clicked polygon color
                    previousClickedSggNm?.let { clearPolygonColor(it) }
                }


                // 구군에따른 폴리곤 색 변경
                val fillColor = Color.RED
                polygon.fillColor = fillColor

                clickedPolygon = polygon
                previousClickedSggNm = sggNm
            }
        }
    }

    // 다른 폴리곤 선택 후 색 변경
    private fun clearPolygonColor(sggNm: String) {
        val previousPolygon = polygonList.firstOrNull { polygon ->
            val index = polygon.tag as? Int
            val polygonSggNm = sggNmList.getOrNull(index ?: -1)
            polygonSggNm == sggNm
        }

        previousPolygon?.fillColor = Color.TRANSPARENT
    }

    private fun loadJSONFromAsset(): String? {
        return try {
            val inputStream: InputStream = resources.openRawResource(R.raw.simplebusan)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }

    }

    private fun drawPolygon(coordinates: JSONArray): PolygonOptions {
        val polygonPoints = mutableListOf<LatLng>()
        for (i in 0 until coordinates.length()) {
            val polygon = coordinates.getJSONArray(i)
            for (j in 0 until polygon.length()) {
                val point = polygon.getJSONArray(j)
                val lat = point.getDouble(1)
                val lng = point.getDouble(0)
                val latLng = LatLng(lat, lng)
                polygonPoints.add(latLng)
            }
        }

        // Add the first point as the last point to close the polygon
        val firstPoint = polygonPoints.firstOrNull()
        if (firstPoint != null) {
            polygonPoints.add(firstPoint)
        }

        val polygonOptions = PolygonOptions()
            .addAll(polygonPoints)
            .clickable(true)

        return polygonOptions
    }

}


