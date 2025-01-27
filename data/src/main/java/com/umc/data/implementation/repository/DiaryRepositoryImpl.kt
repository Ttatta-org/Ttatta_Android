package com.umc.data.implementation.repository

import com.umc.core.model.CategoryInfo
import com.umc.core.model.Diary
import com.umc.core.model.DiaryForCard
import com.umc.core.model.Footprint
import com.umc.core.repository.DiaryRepository
import com.umc.data.api.ServerApi
import com.umc.data.api.dto.server.CategoryDetail
import com.umc.data.api.dto.server.CreateCategoryDTO
import com.umc.data.api.dto.server.EditDTO
import com.umc.data.api.dto.server.KeepDTO
import com.umc.data.api.dto.server.MapDTO
import com.umc.data.api.dto.server.ModifyCategoryDTO
import com.umc.data.api.dto.server.PostDTO
import com.umc.data.api.dto.server.SearchDTO
import com.umc.data.api.withAuth
import com.umc.data.preference.AuthPreference
import com.umc.design.CategoryColor
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference
): DiaryRepository {

    override suspend fun getDiaries(page: Int, date: LocalDate?): List<Diary> {
        val body = KeepDTO(date = date?.atStartOfDay())
        val response = serverApi.withAuth(authPreference) {
            getKeepDiary(requestNum = page, request = body)
        }
        return response.diaryList?.map {
            Diary(
                id = it.diaryId!!,
                date = it.date!!,
                content = it.content!!,
                imageUrl = it.image!!,
                locationName = "API 상에서 지원하지 않음",
            )
        } ?: listOf()
    }

    override suspend fun getDiaries(page: Int, searchWord: String): List<Diary> {
        val body = SearchDTO(searchContent = searchWord)
        val response = serverApi.withAuth(authPreference) {
            searchDiary(requestNum = page, request = body)
        }
        return response.diaryList?.map {
            Diary(
                id = it.diaryId!!,
                date = it.date!!,
                content = it.content!!,
                imageUrl = it.image!!,
                locationName = "API 상에서 지원하지 않음",
            )
        } ?: listOf()
    }

    override suspend fun getDiaries(page: Int, latitude: Double, longitude: Double): DiaryForCard {
        val body = MapDTO(latitude = latitude, longitude = longitude)
        val response = serverApi.withAuth(authPreference) {
            getMapDiary(requestNum = page, request = body)
        }
        return DiaryForCard(
            id = response.diaryId!!,
            date = response.date!!.toLocalDate(),
            content = response.content!!,
            imageUrl = response.image!!
        )
    }

    override suspend fun getAllFootprints(): List<Footprint> {
        TODO("Not yet implemented")
    }

    override suspend fun createDiary(
        categoryId: Long,
        date: LocalDateTime,
        content: String,
        image: File,
        latitude: Double,
        longitude: Double,
        locationName: String
    ) {
        val request = PostDTO(
            diaryCategoryId = categoryId,
            content = content,
            date = date,
            latitude = latitude,
            longitude = longitude,
            locationName = locationName,
        )
        val part = MultipartBody.Part.createFormData(
            image.nameWithoutExtension,
            image.name,
            RequestBody.create(
                MediaType.parse("image/${image.extension}"),
                image.readBytes()
            )
        )
        serverApi.withAuth(authPreference) {
            postDiary(request = request, image = part)
        }
    }

    override suspend fun modifyDiary(diaryId: Long, content: String) {
        val body = EditDTO(content = content)
        serverApi.withAuth(authPreference) {
            editDiary(diaryId = diaryId, body = body)
        }
    }

    override suspend fun deleteDiary(diaryId: Long) {
        serverApi.withAuth(authPreference) {
            deleteDiary(diaryId = diaryId)
        }
    }

    override suspend fun getAllCategoryInfo(): List<CategoryInfo> {
        val response = serverApi.withAuth(authPreference) { getCategoryCounts() }
        return response.categoryDetails?.map {
            CategoryInfo(
                id = it.categoryId!!,
                name = it.categoryName!!,
                color = when (it.categoryColor) {
                    CategoryDetail.CategoryColor.RED -> CategoryColor.RED
                    CategoryDetail.CategoryColor.ORANGE -> CategoryColor.ORANGE
                    CategoryDetail.CategoryColor.YELLOW -> CategoryColor.YELLOW
                    CategoryDetail.CategoryColor.GREEN -> CategoryColor.GREEN
                    CategoryDetail.CategoryColor.SKYBLUE -> CategoryColor.TURQUOISE
                    CategoryDetail.CategoryColor.BLUE -> CategoryColor.BLUE
                    CategoryDetail.CategoryColor.INDIGO -> CategoryColor.NAVY
                    CategoryDetail.CategoryColor.VIOLET -> CategoryColor.PURPLE
                    CategoryDetail.CategoryColor.BROWN -> CategoryColor.BROWN
                    CategoryDetail.CategoryColor.PINK -> CategoryColor.PINK
                    CategoryDetail.CategoryColor.WHITE -> CategoryColor.WHITE
                    CategoryDetail.CategoryColor.BLACK -> CategoryColor.BLACK
                    else -> null
                },
                count = it.diaryCount!!,
            )
        } ?: listOf()
    }

    override suspend fun createCategory(name: String, color: CategoryColor?) {
        val body = CreateCategoryDTO(
            categoryName = name,
            categoryColor = when (color) {
                CategoryColor.RED -> CreateCategoryDTO.CategoryColor.RED
                CategoryColor.ORANGE -> CreateCategoryDTO.CategoryColor.ORANGE
                CategoryColor.YELLOW -> CreateCategoryDTO.CategoryColor.YELLOW
                CategoryColor.GREEN -> CreateCategoryDTO.CategoryColor.GREEN
                CategoryColor.TURQUOISE -> CreateCategoryDTO.CategoryColor.SKYBLUE
                CategoryColor.BLUE -> CreateCategoryDTO.CategoryColor.BLUE
                CategoryColor.NAVY -> CreateCategoryDTO.CategoryColor.INDIGO
                CategoryColor.PURPLE -> CreateCategoryDTO.CategoryColor.VIOLET
                CategoryColor.BROWN -> CreateCategoryDTO.CategoryColor.BROWN
                CategoryColor.PINK -> CreateCategoryDTO.CategoryColor.PINK
                CategoryColor.WHITE -> CreateCategoryDTO.CategoryColor.WHITE
                CategoryColor.BLACK -> CreateCategoryDTO.CategoryColor.BLACK
                else -> null
            }
        )
        serverApi.withAuth(authPreference) {
            createCategory(body = body)
        }
    }

    override suspend fun modifyCategory(
        categoryId: Long,
        name: String,
        color: CategoryColor?
    ) {
        val body = ModifyCategoryDTO(
            categoryName = name,
            categoryColor = when (color) {
                CategoryColor.RED -> "RED"
                CategoryColor.ORANGE -> "ORANGE"
                CategoryColor.YELLOW -> "YELLOW"
                CategoryColor.GREEN -> "GREEN"
                CategoryColor.TURQUOISE -> "SKYBLUE"
                CategoryColor.BLUE -> "BLUE"
                CategoryColor.NAVY -> "INDIGO"
                CategoryColor.PURPLE -> "VIOLET"
                CategoryColor.BROWN -> "BROWN"
                CategoryColor.PINK -> "PINK"
                CategoryColor.WHITE -> "WHITE"
                CategoryColor.BLACK -> "BLACK"
                else -> null
            }
        )
        serverApi.withAuth(authPreference) {
            modifyCategory(categoryId = categoryId, body = body)
        }
    }

    override suspend fun deleteCategory(categoryId: Long) {
        serverApi.withAuth(authPreference) {
            deleteCategory(categoryId = categoryId)
        }
    }

    override suspend fun deleteCategoryAndAllIncludedDiaries(categoryId: Long) {
        serverApi.withAuth(authPreference) {
            deleteCategoryWithDiaries(categoryId = categoryId)
        }
    }
}