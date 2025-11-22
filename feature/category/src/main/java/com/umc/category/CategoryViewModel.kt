package com.umc.category

import androidx.lifecycle.ViewModel
import com.umc.core.model.CategoryInfo
import com.umc.core.repository.DiaryRepository
import com.umc.design.CategoryColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
) : ViewModel() {

    private val categoryListState = MutableStateFlow<List<CategoryInfo>?>(null)

    val categoryList: StateFlow<List<CategoryInfo>?> = categoryListState

    suspend fun getCategoryListFromServer() {
        categoryListState.value = null
        categoryListState.value = diaryRepository.getAllCategoryInfo()
    }

    suspend fun createCategory(
        name: String,
        color: CategoryColor,
    ) {
        diaryRepository.createCategory(name = name, color = color)
        getCategoryListFromServer()
    }

    suspend fun modifyCategory(
        id: Long,
        name: String,
        color: CategoryColor,
    ) {
        diaryRepository.modifyCategory(categoryId = id, name = name, color = color)
        getCategoryListFromServer()
    }

    suspend fun deleteCategory(id: Long) {
        diaryRepository.deleteCategory(categoryId = id)
        getCategoryListFromServer()
    }

    suspend fun deleteCategoryAndAllIncludedDiaries(id: Long) {
        diaryRepository.deleteCategoryAndAllIncludedDiaries(categoryId = id)
        getCategoryListFromServer()
    }
}