package com.umc.footprint

import androidx.lifecycle.ViewModel
import com.naver.maps.map.NaverMap

class FootprintViewModel : ViewModel() {
    private var map: NaverMap? = null

    fun onMapReady(map: NaverMap) {
        this.map = map
    }

    fun getViewingPosition(): Pair<Double, Double>? {
        return map?.cameraPosition?.target?.let { target ->
            target.latitude to target.longitude
        }
    }
}