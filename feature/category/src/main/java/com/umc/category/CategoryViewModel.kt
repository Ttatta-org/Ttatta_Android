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

    val newCategoryNameInputFieldValue = MutableStateFlow("")
    val newCategoryColorSelectedValue = MutableStateFlow(CategoryColor.entries.first())
    val selectedCategory = MutableStateFlow<CategoryInfo?>(null)
    val modifyingCategoryNameInputFieldValue = MutableStateFlow("")
    val modifyingCategoryColorSelectedValue = MutableStateFlow(CategoryColor.entries.first())

    suspend fun getCategoryListFromServer() {
        categoryListState.value = null
        categoryListState.value = diaryRepository.getAllCategoryInfo()
    }

    suspend fun createCategory() {
        val name = newCategoryNameInputFieldValue.value
        if (name.isBlank()) throw IllegalStateException()
        if (name == CategoryConstraints.NON_EDITABLE_CATEGORY_NAME) throw IllegalArgumentException()

        diaryRepository.createCategory(
            name = newCategoryNameInputFieldValue.value,
            color = newCategoryColorSelectedValue.value,
        )

        getCategoryListFromServer()
    }

    suspend fun modifyCategory() {
        val selectedCategory = selectedCategory.value ?: throw IllegalStateException()

        val name = modifyingCategoryNameInputFieldValue.value
        if (name.isBlank()) throw IllegalStateException()
        if (name == CategoryConstraints.NON_EDITABLE_CATEGORY_NAME) throw IllegalArgumentException()

        diaryRepository.modifyCategory(
            categoryId = selectedCategory.id,
            name = name,
            color = modifyingCategoryColorSelectedValue.value
        )

        getCategoryListFromServer()
    }

    suspend fun deleteCategory() {
        val selectedCategory = selectedCategory.value ?: throw IllegalStateException()
        diaryRepository.deleteCategory(categoryId = selectedCategory.id)
        getCategoryListFromServer()
    }

    suspend fun deleteCategoryAndAllIncludedDiaries() {
        val selectedCategory = selectedCategory.value ?: throw IllegalStateException()
        diaryRepository.deleteCategoryAndAllIncludedDiaries(categoryId = selectedCategory.id)
        getCategoryListFromServer()
    }
}