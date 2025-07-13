package com.umc.footprint.implementation

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.clustering.ClusteringKey
import com.umc.footprint.core.MapMarker

class MarkerKey(val mapMarker: MapMarker) : ClusteringKey {
    override fun getPosition(): LatLng {
        return LatLng(mapMarker.latitude, mapMarker.longitude)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is MarkerKey) return false
        return mapMarker == other.mapMarker
    }

    override fun hashCode() = mapMarker.hashCode()
}