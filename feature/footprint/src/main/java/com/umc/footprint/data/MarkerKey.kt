package com.umc.footprint.data

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.clustering.ClusteringKey
import com.naver.maps.map.overlay.Marker
import com.umc.footprint.core.MapMarker

class MarkerKey(val mapMarker: MapMarker) : ClusteringKey {
    var naverMarker: Marker? = null

    override fun getPosition(): LatLng {
        return LatLng(mapMarker.latitude, mapMarker.longitude)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is MarkerKey) return false
        return mapMarker == other.mapMarker
    }

    override fun hashCode() = mapMarker.hashCode()
}