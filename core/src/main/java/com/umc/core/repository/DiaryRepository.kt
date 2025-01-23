package com.umc.core.repository

import com.umc.design.CategoryColor
import com.umc.core.model.CategoryInfo
import com.umc.core.model.Diary
import com.umc.core.model.DiaryForCard
import com.umc.core.model.Footprint
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

interface DiaryRepository {
    // "일기보관함" 화면에서 쓰이는 일기 정보를 가져옵니다.
    suspend fun getDiaries(page: Int, date: LocalDate? = null): List<Diary>  // 날짜로 찾기
    suspend fun getDiaries(page: Int, searchWord: String): List<Diary>  // 검색어로 찾기

    // "나의 발자국" 화면에서 쓰이는 일기 정보를 가져옵니다.
    suspend fun getAllFootprints(): List<Footprint>
    suspend fun getDiaries(page: Int, latitude: Double, longitude: Double): DiaryForCard

    suspend fun createDiary(
        categoryId: Long,
        date: LocalDateTime,
        content: String,
        image: File,
        latitude: Double,
        longitude: Double,
        locationName: String,
    )

    suspend fun modifyDiary(diaryId: Long, content: String)
    suspend fun deleteDiary(diaryId: Long)

    suspend fun getAllCategoryInfo(): List<CategoryInfo>
    suspend fun createCategory(name: String, color: CategoryColor?)
    suspend fun modifyCategory(categoryId: Long, name: String, color: CategoryColor?)
    suspend fun deleteCategory(categoryId: Long)
    suspend fun deleteCategoryAndAllIncludedDiaries(categoryId: Long)
}