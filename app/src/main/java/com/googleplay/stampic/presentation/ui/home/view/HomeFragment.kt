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
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.googleplay.stampic.domain.model.DialogInfo
import com.googleplay.stampic.presentation.ui.base.BaseFragment
import com.googleplay.stampic.presentation.util.DialogFragmentUtil
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
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
import com.googleplay.stampic.presentation.ui.detail.view.DetailActivity
import com.googleplay.stampic.presentation.ui.home.adapter.HomeListRecyclerViewAdapter
import com.googleplay.stampic.presentation.ui.home.viewmodel.HomeViewModel

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home), OnMapReadyCallback {


    private lateinit var homeListViewAdapter: HomeListRecyclerViewAdapter
    private lateinit var mContext: Context
    private lateinit var googleMap:GoogleMap

    private val homeViewModel: HomeViewModel by viewModels()
    private val DESIRED_ZOOM_LEVEL = 10f
    private val markerList: MutableList<Marker> = mutableListOf()
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
            }
            else -> {
                // 권한 거부
                permissionDialog()
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
        }

    }

    // 데이터 바인딩 이후에 수행되는 초기화 작업
    override fun initAfterBinding() {
        Log.d("dd", "initAfterBinding 실행")
        val searchView: SearchView = binding.searchView
        val imageView: ImageView = binding.homeToolImg
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 검색 버튼을 눌렀을 때 동작
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 검색어가 변경될 때 동작
                val filteredList = homeViewModel.attrList.value?.filter { attrList ->

                    // place, contents에서 검색
                    attrList.place.contains(newText.toString(), ignoreCase = true) ||
                            attrList.contents.contains(newText.toString(), ignoreCase = true)
                }
                homeListViewAdapter.submitList(filteredList)
                return true
            }
        })

        // SearchView의 포커스 변화 리스너 설정
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // SearchView에 포커스가 있는 경우, ImageView를 숨김
                imageView.visibility = View.GONE
            } else {
                // SearchView에서 포커스가 벗어난 경우, 현재 검색어 유무에 따라 ImageView 가시성을 조절
                if (searchView.query.isEmpty()) {
                    imageView.visibility = View.VISIBLE
                } else {
                    imageView.visibility = View.GONE
                }
            }
        }

        homeListViewAdapter.setOnItemClickListener {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("image", it.image)
            intent.putExtra("title", it.title)
            intent.putExtra("contents", it.contents)
            intent.putExtra("lat", it.lat)
            intent.putExtra("lng", it.lng)
            intent.putExtra("addr", it.addr)
            intent.putExtra("place", it.place)
            startActivity(intent)
        }

        val recyclerView = binding.activityGroup.rvHome
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.setOnItemReselectedListener { menuItem ->
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

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = recyclerView.layoutManager?.childCount ?: 0
                val totalItemCount = recyclerView.layoutManager?.itemCount ?: 0
                val firstVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                    // 리스트뷰의 끝에 도달한 경우
                    bottomNavigationView.visibility = View.GONE
                } else {
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
        })

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
    private var previousClickedPolygon: Polygon? = null

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

        // 폴리곤 선택 시 -> 색, 확대, 리스트
        googleMap.setOnPolygonClickListener { polygon ->

            // 폴리곤 색 변경
            clickedPolygon?.fillColor = Color.TRANSPARENT
            clickedPolygon = null

            // 선택된 폴리곤의 구군 가져오기
            val index  = polygon.tag as? Int
            val sggNm = sggNmList.getOrNull(index ?: -1)
            Log.d("dd",sggNm.toString())

            if (sggNm != null && desiredSggNmList.contains(sggNm)) {
                if (previousClickedPolygon  != null && previousClickedPolygon != polygon) {
                    // 이전에 선택한 폴리곤 색 초기화
                    previousClickedPolygon?.fillColor = Color.TRANSPARENT
                    // 이전에 선택한 폴리곤의 마커 제거
                    removeMarkers()
                }

                // 구군에따른 폴리곤 색 변경
                val fillColor = Color.RED
                polygon.fillColor = fillColor

                // 선택한 폴리곤을 저장하여 이후에 초기화 또는 비교에 사용
                previousClickedPolygon = polygon

                clickedPolygon = polygon
                previousClickedSggNm = sggNm

                // 클릭한 다각형의 좌표 가져오기
                val points = polygon.points.toList()

                // 좌표로부터 경계 계산
                val builder = LatLngBounds.builder()
                for (point in points) {
                    builder.include(point)
                }
                val bounds = builder.build()

                // 지도 확대
                val padding = 200 // 확대하는 정도
                val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                googleMap.animateCamera(cameraUpdate, object : GoogleMap.CancelableCallback {

                    override fun onFinish() {

                        // 카메라 위치 이동
                        val newCameraPosition = CameraPosition.builder()
                            .target(polygon.points[0]) // 폴리곤의 첫 번째 좌표를 타겟으로 설정
                            .zoom(googleMap.cameraPosition.zoom) // 현재 줌 레벨 유지
                            .build()
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition))

                        // attrList에서 gugunNm이 sggNm과 일치하는 항목 필터링
                        val filteredList =
                            homeViewModel.attrList.value?.filter { it.gugunNm == sggNm }
                        Log.d("dd", "submitList 실행")
                        homeListViewAdapter.submitList(filteredList)
                        //Log.d("dd", "filteredList -> " + filteredList)

                    }
                    override fun onCancel() {
                        // 카메라 이동이 취소된 경우의 처리
                    }
                })
            } else {
                if (previousClickedPolygon  != null) {
                    // 이전에 선택한 폴리곤 색 초기화
                    previousClickedPolygon?.fillColor = Color.TRANSPARENT
                    // 이전에 선택한 폴리곤의 마커 제거
                    removeMarkers()
                    previousClickedPolygon = null
                    Log.d("dd","초기화 실행")
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(busanLatLng, 10f))
                }

                // 폴리곤 선택 x -> 전체 attrList 가져오기
                homeListViewAdapter.submitList(homeViewModel.attrList.value)

            }

        }

        // 구글맵 카메라 이벤트 리스너 등록
        googleMap.setOnCameraIdleListener {
            val zoomLevel = googleMap.cameraPosition.zoom

            // 확대된 경우에만 마커 추가
            if (zoomLevel > DESIRED_ZOOM_LEVEL) {
                addMarkersFromFilteredList()
            } else {
                // 확대되지 않은 경우에는 기존의 마커 제거
                removeMarkers()
            }

        }
    }

    // 마커 추가 함수
    private fun addMarkersFromFilteredList() {
        // 이전에 추가된 마커 제거
        removeMarkers()

        val filteredList = homeViewModel.attrList.value // filteredList 변수 추가

        // 선택한 폴리곤의 SGG_NM 가져오기
        val selectedSggNm = previousClickedPolygon?.tag as? String

        for (item in filteredList.orEmpty()) {
            val lat = item.lat
            val lng = item.lng
            val sggNm = item.gugunNm

            if (selectedSggNm == sggNm) {
                val markerOptions = MarkerOptions()
                    .position(LatLng(lat.toDouble(), lng.toDouble()))
                    .title(item.place)

                val marker = googleMap.addMarker(markerOptions)
                marker?.let {
                    markerList.add(it)
                    //Log.d("dd", "markerList" + markerList)
                }
            }
        }
    }

    // 마커 초기화
    private fun removeMarkers() {
        for (marker in markerList) {
            marker.remove()
        }
        markerList.clear()
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

        // 처음이랑 끝 좌표 연결
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