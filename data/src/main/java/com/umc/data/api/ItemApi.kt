package com.umc.data.api

import com.umc.data.api.dto.BaseResponse
import com.umc.data.api.dto.server.IdListDTO
import com.umc.data.api.dto.server.ItemBuyResultDTO
import com.umc.data.api.dto.server.ItemDisrobeResultDTO
import com.umc.data.api.dto.server.ItemEquipResultDTO
import com.umc.data.api.dto.server.ItemMyItemListDTO
import com.umc.data.api.dto.server.ItemShopListDTO
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ItemApi {
    // 아이템 구매
    @PATCH("/items/{itemId}")
    suspend fun buyItem(
        @Path("itemId") itemId: Long
    ): BaseResponse<ItemBuyResultDTO>

    // 아이템 착용
    @PATCH("/items/equip/{itemId}")
    suspend fun equipItem(
        @Path("itemId") itemId: Long
    ): BaseResponse<ItemEquipResultDTO>

    // 아이템 해제
    @PATCH("/items/disrobe/{itemId}")
    suspend fun disrobeItem(
        @Path("itemId") itemId: Long
    ): BaseResponse<ItemDisrobeResultDTO>

    // 미소유 아이템 (shop) 조회
    @GET("/items/shop")
    suspend fun getShopItems(): BaseResponse<ItemShopListDTO>

    // 소유 아이템 조회
    @GET("/items/owned")
    suspend fun getOwnedItems(): BaseResponse<ItemMyItemListDTO>

    // 착용한 아이템 조회
    @GET("/items/equipped")
    suspend fun getEquippedItems(): BaseResponse<IdListDTO>
}