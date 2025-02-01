package com.umc.category

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.model.CategoryInfo
import com.umc.core.repository.DiaryRepository
import com.umc.design.CategoryColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
): ViewModel() {
    private val categoryListState = mutableStateOf<List<CategoryInfo>>(listOf())

    val categoryList get() = categoryListState.value

    init {
        getCategoryListFromServer()
    }

    private fun getCategoryListFromServer(
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                categoryListState.value = diaryRepository.getAllCategoryInfo()
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun createCategory(
        name: String,
        color: CategoryColor?,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                diaryRepository.createCategory(name = name, color = color)
                getCategoryListFromServer(onSucceed = { onSucceed() }, onFailed = { throw it })
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun modifyCategory(
        id: Long,
        name: String,
        color: CategoryColor?,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                diaryRepository.modifyCategory(categoryId = id, name = name, color = color)
                getCategoryListFromServer(onSucceed = { onSucceed() }, onFailed = { throw it })
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun deleteCategory(
        id: Long,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                diaryRepository.deleteCategory(categoryId = id)
                getCategoryListFromServer(onSucceed = { onSucceed() }, onFailed = { throw it })
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun deleteCategoryAndAllIncludedDiaries(
        id: Long,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                diaryRepository.deleteCategoryAndAllIncludedDiaries(categoryId = id)
                getCategoryListFromServer(onSucceed = { onSucceed() }, onFailed = { throw it })
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }
}