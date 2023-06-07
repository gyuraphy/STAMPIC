package com.googleplay.stampic.presentation.ui.home.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.viewModels
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.googleplay.stampic.R
import com.googleplay.stampic.databinding.FragmentHomeBinding
import com.googleplay.stampic.domain.model.DialogInfo
import com.googleplay.stampic.presentation.ui.base.BaseFragment
import com.googleplay.stampic.presentation.ui.detail.view.DetailActivity
import com.googleplay.stampic.presentation.ui.home.adapter.HomeListRecyclerViewAdapter
import com.googleplay.stampic.presentation.ui.home.viewmodel.HomeViewModel
import com.googleplay.stampic.presentation.util.DialogFragmentUtil
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import kotlin.math.*

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home), OnMapReadyCallback {


    private lateinit var homeListViewAdapter: HomeListRecyclerViewAdapter
    private lateinit var mContext: Context
    private lateinit var googleMap: GoogleMap
    private lateinit var googleApiClient: GoogleApiClient
    private val homeViewModel: HomeViewModel by viewModels()
    private var previousZoomLevel = 10f

    //private val DESIRED_ZOOM_LEVEL = 9f
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
        Log.d("dd", "initStartView 실행")
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
            Log.d("dd", "getAttrList() 실행")
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

        // FusedLocationProviderClient 객체 생성
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)

        // 위치 업데이트 요청
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).apply {
            setMinUpdateDistanceMeters(1F)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val latestLocation = locationResult.lastLocation
                // 최신 위치 정보를 사용하여 필요한 작업 수행
                val currentLat = latestLocation?.latitude
                val currentLng = latestLocation?.longitude
                Log.d("dd","나의 현재 위도:"+currentLat)
                Log.d("dd","나의 현재 경도:"+currentLng)
                val attrList = homeViewModel.attrList.value
                if (!attrList.isNullOrEmpty()) {
                    for (attrInfo in attrList) {
                        val targetLat = attrInfo.lat.toDouble()
                        val targetLng = attrInfo.lng.toDouble()

                        // 현재 위치와의 거리 계산
//                        val distance = getDistance(currentLat, currentLng, targetLat, targetLng)

                        // 15m 반경 안에 접근할 경우 데이터베이스에 삽입
//                        if (distance <= 15) {
//                            // stamp 테이블에 데이터 삽입하는 로직 작성
//                            // ...
//                        }
                    }
                }
            }
        }

        // 위치 권한 확인
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val currentLat = location.latitude
                        val currentLng = location.longitude
                        // 위치 정보를 사용하여 필요한 작업 수행
                        Log.d("dd","나의 현재 위도:"+currentLat)
                        Log.d("dd","나의 현재 경도:"+currentLng)
                        val attrList = homeViewModel.attrList.value
                        if (attrList != null) {
                            Log.d("dd", "attrList는 가져와졌을까?"+attrList.size)
                        }
                        if (!attrList.isNullOrEmpty()) {
                            for (attrInfo in attrList) {
                                val targetLat = attrInfo.lat.toDouble()
                                val targetLng = attrInfo.lng.toDouble()

                                // 현재 위치와의 거리 계산
                                val distance = getDistance(currentLat, currentLng, targetLat, targetLng)

                                // 15m 반경 안에 접근할 경우 데이터베이스에 삽입
                                if (distance <= 15) {
                                    // stamp 테이블에 데이터 삽입하는 로직 작성
                                    // ...
                                }
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // 위치 정보를 가져오는 도중에 오류가 발생한 경우 처리
                }
            // 위치 업데이트 요청
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    // 거리 계산
    private fun getDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val R = 6371 // 지구의 반지름 (단위: km)

        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lng2 - lng1)

        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(lonDistance / 2) * sin(lonDistance / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c * 1000 // 거리 반환 (단위: m)
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

    private var previousClickedSggNm: String? = null
    private var previousClickedPolygon: Polygon? = null

    private val desiredSggNmList = listOf(
        "동래구", "남구", "영도구", "동구", "서구", "중구",
        "부산진구", "연제구", "강서구", "금정구", "사하구",
        "해운대구", "북구", "기장군", "사상구", "수영구"
    )

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

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // 시작위치 부산
        val busanLatLng = LatLng(35.1796, 129.0750)

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(busanLatLng, 10f))

        googleMap.uiSettings.isZoomControlsEnabled = true

        // 선택된 폴리곤의 색상 (파란색과 50%의 투명도)
        val selectedFillColor = Color.argb(128, 0, 79, 128)

        // 선택되지 않은 폴리곤의 색상 (투명한 색)
        val unselectedFillColor = Color.argb(51, 0,79, 128)

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
                        polygonOptions.fillColor(unselectedFillColor)
                        googleMap.addPolygon(polygonOptions)
                        val polygon = googleMap.addPolygon(polygonOptions)
                        polygon.tag = i // Set the index as a tag to retrieve SGG_NM later
                        polygonList.add(polygon)

                    } else if (type == "MultiPolygon") {

                        for (j in 0 until coordinates.length()) {
                            val polygonCoordinates = coordinates.getJSONArray(j)
                            val polygonOptions = drawPolygon(polygonCoordinates)
                            polygonOptions.fillColor(unselectedFillColor)
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


            // 현재 선택한 폴리곤과 이전에 선택한 폴리곤이 같은 경우
            if (previousClickedPolygon == polygon) {
                var recyclerView = binding.activityGroup.rvHome

                // 폴리곤 선택 x -> 전체 명소 리스트 가져오기
                homeListViewAdapter.submitList(homeViewModel.attrList.value)

                // 이전에 선택한 폴리곤의 색상 초기화
                previousClickedPolygon?.fillColor = unselectedFillColor

                // 선택한 폴리곤 초기화
                previousClickedPolygon = null
                previousClickedSggNm = null

                Log.d("dd", "지도 초기화")
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(busanLatLng, 10f))

                //리스트 제일 위로
//                recyclerView.smoothScrollToPosition(0)

                removeMarkers()

            } else {

                // 이전에 선택한 폴리곤의 색상 초기화
                previousClickedPolygon?.fillColor = unselectedFillColor

                // 선택한 폴리곤의 색상 변경
                polygon.fillColor = selectedFillColor

                // 선택한 폴리곤을 저장하여 이후에 초기화 또는 비교에 사용
                previousClickedPolygon = polygon

                // 선택된 폴리곤의 구군 가져오기
                val index = polygon.tag as? Int
                val sggNm = sggNmList.getOrNull(index ?: -1)
                Log.d("dd", sggNm.toString())
                previousClickedSggNm = sggNm


                // sggNm이 null x, 구군네임 안에 포함되어있다면
                if (sggNm != null && desiredSggNmList.contains(sggNm)) {

                    val points = polygon.points.toList()
                    val builder = LatLngBounds.builder()
                    for (point in points) {
                        builder.include(point)
                    }
                    val bounds = builder.build()
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10))

                    // attrList에서 gugunNm이 sggNm과 일치하는 리스트 필터링
                    val filteredList =
                        homeViewModel.attrList.value?.filter { it.gugunNm == sggNm }
                    Log.d("dd", "submitList 실행")

                    // 필터링 한 리스트 띄우기
                    homeListViewAdapter.submitList(filteredList)

                } else {

                    // 폴리곤 선택 x -> 전체 명소 리스트 가져오기
                    homeListViewAdapter.submitList(homeViewModel.attrList.value)
                }

                // 지도 클릭 시
                googleMap.setOnMapClickListener { point ->

                    var recyclerView = binding.activityGroup.rvHome

                    // 이전에 선택한 폴리곤의 색상 초기화
                    previousClickedPolygon?.fillColor = unselectedFillColor

                    // 선택한 폴리곤 초기화
                    previousClickedPolygon = null
                    previousClickedSggNm = null

                    // 폴리곤 선택 x -> 전체 명소 리스트 가져오기
                    homeListViewAdapter.submitList(homeViewModel.attrList.value)

                    Log.d("dd", "지도 초기화")
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(busanLatLng, 10f))

                    //리스트 제일 위로
//                    recyclerView.smoothScrollToPosition(0)

                    removeMarkers()
                }

                // 구글맵 카메라 이벤트 리스너 등록
                // 확대 시 마커 추가
                googleMap.setOnCameraIdleListener {

                    val zoomLevel = googleMap.cameraPosition.zoom

                    // 이전의 줌 레벨과 현재 줌 레벨을 비교하여 확대/축소 여부 확인
                    if (zoomLevel > previousZoomLevel) {
                        // 확대된 경우에는 마커 추가
                        addMarkersFromFilteredList()
                    } else {
                        // 축소된 경우에는 기존의 마커 제거
                        removeMarkers()
                    }
                    // 현재 줌 레벨을 이전 줌 레벨로 업데이트
                    previousZoomLevel = zoomLevel

                }
            }
        }
    }

    //폴리곤 그리기
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
            .strokeWidth(5f)
            .addAll(polygonPoints)
            .clickable(true)

        return polygonOptions
    }

    private fun addMarkersFromFilteredList() {
        // 이전에 추가된 마커 제거
        removeMarkers()

        // filteredList -> 필터링 된 명소 리스트
        val filteredList = homeViewModel.attrList.value

        // 선택한 폴리곤의 SGG_NM 가져오기
        val selectedSggNm = previousClickedSggNm

        for (item in filteredList.orEmpty()) {
            val lat = item.lat
            val lng = item.lng
            val sggNm = item.gugunNm

            // 필터링된 리스트 내의 구군네임과 선택한 구군네임이 일치하는 경우에만 마커 추가
            if (selectedSggNm == sggNm) {
                val markerOptions = MarkerOptions()
                    .position(LatLng(lat.toDouble(), lng.toDouble()))
                    .title(item.place)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.stamp_menu))

                val marker = googleMap.addMarker(markerOptions)
                marker?.let {
                    markerList.add(it)
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

}