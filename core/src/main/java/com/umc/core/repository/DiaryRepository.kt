package com.umc.core.repository

import com.umc.design.CategoryColor
import com.umc.core.model.CategoryInfo
import com.umc.core.model.DailySummary
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

    // 하루 요약 기능
    suspend fun getDailySummary(date: LocalDate): DailySummary?  // null을 반환할 경우, 아직 하루 요약이 생성되지 않음
    suspend fun generateDailySummary(date: LocalDate)  // 하루 요약 생성 또는 재생성을 요청

    suspend fun getAllRecordedDates(): List<LocalDate>

    // "나의 발자국" 화면에서 쓰이는 일기 정보를 가져옵니다.
    suspend fun getAllFootprints(categoryId: Long? = null): List<Footprint>
    suspend fun getDiaries(page: Int, clusterId: Long, categoryId: Long? = null): DiaryForCard

    suspend fun createDiary(
        categoryId: Long,
        date: LocalDateTime,
        content: String,
        image: File,
        latitude: Double,
        longitude: Double,
        locationName: String,
    )

    suspend fun modifyDiary(
        diaryId: Long,
        categoryId: Long? = null,
        content: String? = null,
        image: File? = null,
    )

    suspend fun deleteDiary(diaryId: Long)

    suspend fun getAllCategoryInfo(): List<CategoryInfo>
    suspend fun createCategory(name: String, color: CategoryColor?)
    suspend fun modifyCategory(categoryId: Long, name: String, color: CategoryColor?)
    suspend fun deleteCategory(categoryId: Long)
    suspend fun deleteCategoryAndAllIncludedDiaries(categoryId: Long)
}