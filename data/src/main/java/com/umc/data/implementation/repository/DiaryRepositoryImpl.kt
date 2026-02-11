package com.umc.data.implementation.repository

import com.umc.core.model.CategoryInfo
import com.umc.core.model.DailySummary
import com.umc.core.model.Diary
import com.umc.core.model.DiaryForCard
import com.umc.core.model.DiaryForRemind
import com.umc.core.model.Footprint
import com.umc.core.repository.DiaryRepository
import com.umc.data.api.ImageUploadApi
import com.umc.data.api.ServerApi
import com.umc.data.api.dto.server.CategoryDetailDTO
import com.umc.data.api.dto.server.CreateCategoryDTO
import com.umc.data.api.dto.server.EditDTO
import com.umc.data.api.dto.server.MapResultDTO
import com.umc.data.api.dto.server.ModifyCategoryDTO
import com.umc.data.api.dto.server.PostDTO
import com.umc.data.api.dto.server.RemindDiaryDTO
import com.umc.data.api.dto.server.SummarizeDTO
import com.umc.data.exception.ServerException
import com.umc.data.preference.AuthPreference
import com.umc.data.util.AuthenticatedRepository
import com.umc.data.util.getMimeTypeFromExtension
import com.umc.data.util.toOffsetDateTimeInKorea
import com.umc.design.CategoryColor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    override val authPreference: AuthPreference,
    private val serverApi: ServerApi,
    private val imageUploadApi: ImageUploadApi,
) : DiaryRepository, AuthenticatedRepository {

    override suspend fun getDiaries(page: Int, date: LocalDate?): List<Diary> {
        val response = serverApi.withAuth {
            getKeepDiaryList(requestNum = page, date = date?.atStartOfDay())
        }
        return response.diaryList?.map {
            Diary(
                id = it.diaryId!!,
                date = it.date!!.toLocalDateTime(),
                content = it.content!!,
                imageUrl = it.image!!,
                locationName = it.locationName!!,
                categoryId = it.diaryCategoryId!!,
            )
        } ?: listOf()
    }

    override suspend fun getDiaries(page: Int, searchWord: String): List<Diary> {
        val response = serverApi.withAuth {
            getSearchDiaryList(requestNum = page, searchContent = searchWord)
        }
        return response.searchDiaryList?.map {
            Diary(
                id = it.diaryId!!,
                date = it.date!!.toLocalDateTime(),
                content = it.content!!,
                imageUrl = it.image!!,
                locationName = it.locationName!!,
                categoryId = it.diaryCategoryId!!,
            )
        } ?: listOf()
    }

    override suspend fun getDailySummary(date: LocalDate): DailySummary? {
        val date = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val response = try {
            serverApi.withAuth { getDailySummary(date = date) }
        } catch (_: Exception) {
            null
        }

        return response?.let {
            DailySummary(
                summary = response.summaryDiary!!,
                createdTime = response.createdAt!!.toLocalDateTime()
            )
        }
    }

    override suspend fun generateDailySummary(date: LocalDate): DailySummary {
        val dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val existingSummary = try {
            serverApi.withAuth { getDailySummary(date = dateString) }
        } catch (e : Exception) {
            if (e is ServerException && e.code == "SUMMARY_8001") null else throw e
        }

        val body = SummarizeDTO(date = date)

        if (existingSummary != null) {
            val response =
                serverApi.withAuth { regenerateDailySummary(body = body) }

            return DailySummary(
                summary = response.content!!,
                createdTime = response.createdAt!!.toLocalDateTime(),
            )
        } else {
            serverApi.withAuth { generateDailySummary(body = body) }

            return getDailySummary(date = date)!!
        }
    }

    override suspend fun getDiaries(page: Int, clusterId: Long, categoryId: Long?): DiaryForCard {
        val response = serverApi.withAuth {
            getMapDiary(requestNum = page, clusterId = clusterId, diaryCategoryId = categoryId)
        }

        return DiaryForCard(
            id = response.diaryId!!,
            date = response.date!!.toLocalDate(),
            color = when (response.color) {
                MapResultDTO.Color.RED -> CategoryColor.RED
                MapResultDTO.Color.ORANGE -> CategoryColor.ORANGE
                MapResultDTO.Color.YELLOW -> CategoryColor.YELLOW
                MapResultDTO.Color.GREEN -> CategoryColor.GREEN
                MapResultDTO.Color.SKYBLUE -> CategoryColor.TURQUOISE
                MapResultDTO.Color.BLUE -> CategoryColor.BLUE
                MapResultDTO.Color.INDIGO -> CategoryColor.NAVY
                MapResultDTO.Color.VIOLET -> CategoryColor.PURPLE
                MapResultDTO.Color.BROWN -> CategoryColor.BROWN
                MapResultDTO.Color.PINK -> CategoryColor.PINK
                MapResultDTO.Color.WHITE -> CategoryColor.WHITE
                MapResultDTO.Color.BLACK -> CategoryColor.BLACK
                null -> null
            },
            content = response.content!!,
            imageUrl = response.image!!,
        )
    }

    override suspend fun getDiaryForRemind(id: Long): DiaryForRemind {
        val diary = serverApi.withAuth {
            getRemindDiary(diaryId = id)
        }

        return DiaryForRemind(
            id = diary.diaryId!!,
            isClustered = diary.isSingle!!,
            date = diary.date!!.toLocalDate(),
            color = when (diary.color) {
                RemindDiaryDTO.Color.RED -> CategoryColor.RED
                RemindDiaryDTO.Color.ORANGE -> CategoryColor.ORANGE
                RemindDiaryDTO.Color.YELLOW -> CategoryColor.YELLOW
                RemindDiaryDTO.Color.GREEN -> CategoryColor.GREEN
                RemindDiaryDTO.Color.SKYBLUE -> CategoryColor.TURQUOISE
                RemindDiaryDTO.Color.BLUE -> CategoryColor.BLUE
                RemindDiaryDTO.Color.INDIGO -> CategoryColor.NAVY
                RemindDiaryDTO.Color.VIOLET -> CategoryColor.PURPLE
                RemindDiaryDTO.Color.BROWN -> CategoryColor.BROWN
                RemindDiaryDTO.Color.WHITE -> CategoryColor.WHITE
                RemindDiaryDTO.Color.PINK -> CategoryColor.PINK
                RemindDiaryDTO.Color.BLACK -> CategoryColor.BLACK
                null -> null
            },
            content = diary.content!!,
            imageUrl = diary.image!!,
            latitude = diary.latitude!!,
            longitude = diary.longitude!!,
        )
    }

    override suspend fun getAllRecordedDates(): List<LocalDate> {
        val response = serverApi.withAuth { getDiariesDate() }
        return response.diaryDateList?.mapNotNull { it.date } ?: listOf()
    }

    override suspend fun getAllFootprints(categoryId: Long?): List<Footprint> {
        val response = serverApi.withAuth {
            getFootprintDiaryList(
                diaryCategoryId = categoryId,
                lat1 = 40.0,
                lng1 = 140.0,
                lat2 = 30.0,
                lng2 = 140.0,
                lat3 = 30.0,
                lng3 = 120.0,
                lat4 = 40.0,
                lng4 = 120.0,
            )
        }

        return response.footprintList?.map {
            Footprint(
                diaryId = it.diaryId!!,
                categoryId = it.diaryCategoryId!!,
                clusterId = it.clusterId!!,
                isClustered = !it.isSingle!!,
                color = when (it.categoryColor!!) {
                    "RED" -> CategoryColor.RED
                    "ORANGE" -> CategoryColor.ORANGE
                    "YELLOW" -> CategoryColor.YELLOW
                    "GREEN" -> CategoryColor.GREEN
                    "SKYBLUE" -> CategoryColor.TURQUOISE
                    "BLUE" -> CategoryColor.BLUE
                    "INDIGO" -> CategoryColor.NAVY
                    "VIOLET" -> CategoryColor.PURPLE
                    "BROWN" -> CategoryColor.BROWN
                    "PINK" -> CategoryColor.PINK
                    "WHITE" -> CategoryColor.WHITE
                    "BLACK" -> CategoryColor.BLACK
                    else -> null
                },
                latitude = it.latitude!!,
                longitude = it.longitude!!,
            )
        } ?: listOf()
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
        val mime = getMimeTypeFromExtension(image.extension)
            ?: throw IllegalArgumentException("Invalid image extension")

        val presignedUrl = serverApi.withAuth { getPresignedUrl(imageType = mime) }

        imageUploadApi.uploadImage(
            url = presignedUrl.presignedUrl!!,
            contentType = mime,
            image = image.asRequestBody(contentType = mime.toMediaTypeOrNull())
        )

        val request = PostDTO(
            diaryCategoryId = categoryId,
            content = content,
            date = date.toOffsetDateTimeInKorea(),
            latitude = latitude,
            longitude = longitude,
            locationName = locationName,
            objectKey = presignedUrl.objectKey!!
        )

        serverApi.withAuth { createDiary(body = request) }
    }

    override suspend fun modifyDiary(
        diaryId: Long,
        categoryId: Long?,
        content: String?,
        image: File?,
    ) {
        image?.let {
            val mime = getMimeTypeFromExtension(image.extension)
                ?: throw IllegalArgumentException("Invalid image extension")

            val presignedUrl =
                serverApi.withAuth { getEditPresignedUrl(diaryId, mime) }

            imageUploadApi.uploadImage(
                url = presignedUrl.presignedUrl!!,
                contentType = mime,
                image = image.asRequestBody(contentType = mime.toMediaTypeOrNull())
            )
        }

        val request = EditDTO(content = content, diaryCategoryId = categoryId)
        serverApi.withAuth { updateDiary(diaryId = diaryId, body = request) }
    }

    override suspend fun deleteDiary(diaryId: Long) {
        serverApi.withAuth {
            deleteDiary(diaryId = diaryId)
        }
    }

    override suspend fun getAllCategoryInfo(): List<CategoryInfo> {
        val response = serverApi.withAuth { getDiaryCount() }
        return response.categoryDetails?.map {
            CategoryInfo(
                id = it.categoryId!!,
                name = it.categoryName!!,
                color = when (it.categoryColor) {
                    CategoryDetailDTO.CategoryColor.RED -> CategoryColor.RED
                    CategoryDetailDTO.CategoryColor.ORANGE -> CategoryColor.ORANGE
                    CategoryDetailDTO.CategoryColor.YELLOW -> CategoryColor.YELLOW
                    CategoryDetailDTO.CategoryColor.GREEN -> CategoryColor.GREEN
                    CategoryDetailDTO.CategoryColor.SKYBLUE -> CategoryColor.TURQUOISE
                    CategoryDetailDTO.CategoryColor.BLUE -> CategoryColor.BLUE
                    CategoryDetailDTO.CategoryColor.INDIGO -> CategoryColor.NAVY
                    CategoryDetailDTO.CategoryColor.VIOLET -> CategoryColor.PURPLE
                    CategoryDetailDTO.CategoryColor.BROWN -> CategoryColor.BROWN
                    CategoryDetailDTO.CategoryColor.PINK -> CategoryColor.PINK
                    CategoryDetailDTO.CategoryColor.WHITE -> CategoryColor.WHITE
                    CategoryDetailDTO.CategoryColor.BLACK -> CategoryColor.BLACK
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
        serverApi.withAuth {
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
        serverApi.withAuth {
            updateCategory(categoryId = categoryId, body = body)
        }
    }

    override suspend fun deleteCategory(categoryId: Long) {
        serverApi.withAuth {
            deleteCategoryOnly(categoryId = categoryId)
        }
    }

    override suspend fun deleteCategoryAndAllIncludedDiaries(categoryId: Long) {
        serverApi.withAuth {
            deleteCategoryAndAllIncludedDiaries(categoryId = categoryId)
        }
    }
}