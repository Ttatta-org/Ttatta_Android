package com.umc.data

import kotlinx.coroutines.test.runTest
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun geocoderTest() = runTest {
        val geocoder = GeocoderImpl()
        val coordinate = geocoder.convertAddressToCoordinate("인천광역시 미추홀구 미추홀대로 598번길 26")
        val address = geocoder.convertCoordinateToAddress(coordinate.first, coordinate.second)
        println("result: $coordinate, $address")
        assert(true)
    }
}