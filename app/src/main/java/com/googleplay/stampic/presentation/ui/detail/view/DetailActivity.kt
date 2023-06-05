package com.googleplay.stampic.presentation.ui.detail.view

import android.os.Bundle
import androidx.appcompat.widget.Toolbar

import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.googleplay.stampic.R
import com.googleplay.stampic.databinding.ActivityDetailBinding
import com.googleplay.stampic.presentation.ui.base.BaseActivity


class DetailActivity : BaseActivity<ActivityDetailBinding>(R.layout.activity_detail),
    OnMapReadyCallback {
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private var lat: Float = 0.0F
    private var lng: Float = 0.0F
    private var place: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initStartView()
    }

    private fun initStartView() {
        val image = intent.getStringExtra("image")
        val title = intent.getStringExtra("title")
        val contents = intent.getStringExtra("contents")
        lat = intent.getFloatExtra("lat", 0.0F)
        lng = intent.getFloatExtra("lng", 0.0F)
        val addr = intent.getStringExtra("addr")

        // 데이터를 바인딩하기 위해 바인딩 객체 생성
        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = binding.detailMapView
        mapView.onCreate(null)
        mapView.getMapAsync(this)

        // 받은 데이터를 바인딩 객체에 설정
        binding.detailImage.clipToOutline = true
        Glide.with(binding.detailImage.context).load(image)
            .into(binding.detailImage)
        binding.titleText.text = title
        binding.contentsText.text = contents
        binding.addrText.text = addr

        val toolbar: Toolbar = binding.toolBack
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    // 지도 설정
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        googleMap.uiSettings.isZoomControlsEnabled = true

        val markerOptions = MarkerOptions()
            .position(LatLng(lat.toDouble(), lng.toDouble()))
            .title(place)
        googleMap?.addMarker(markerOptions)

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(lat.toDouble(), lng.toDouble()), 15f)
        googleMap?.moveCamera(cameraUpdate)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}