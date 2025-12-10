package com.umc.category

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.umc.category.modal.CategoryAndAllIncludedDiaryDeletionDialog
import com.umc.category.modal.CategoryDeletionDialog
import com.umc.category.modal.CategoryManagementBar
import com.umc.category.model.CategoryAndAllIncludedDiaryDeletionDialogProp
import com.umc.category.model.CategoryDeletionDialogProp
import com.umc.category.model.CategoryListItemProp
import com.umc.category.model.CategoryManagementBarProp
import com.umc.category.screen.CategoryModificationScreen
import com.umc.category.screen.CategoryScreen
import com.umc.core.util.runWithScope
import com.umc.core.util.showToast
import com.umc.design.CategoryColor
import com.umc.design.component.LoadingModal
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

@Composable
fun CategoryApp(
    viewModel: CategoryViewModel,
    topBarTitle: String,
    onBackButtonClicked: () -> Unit,
) {
    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current
    val navigator = rememberNavController()
    val categoryList by viewModel.categoryList.collectAsState()

    val categoryNameInputFieldValue by viewModel.newCategoryNameInputFieldValue.collectAsState()
    val selectedCategoryColor by viewModel.newCategoryColorSelectedValue.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var showCategoryManagementBar by remember { mutableStateOf(false) }
    var showCategoryDeletionDialog by remember { mutableStateOf(false) }
    var showCategoryAndAllIncludedDiaryDeletionDialog by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.runWithScope {
            runCatching { getCategoryListFromServer() }.onFailure {
                context.showToast("카테고리 목록을 불러오는데 실패했습니다.")
            }
        }
    }

    NavHost(
        navController = navigator,
        startDestination = "/",
    ) {
        composable(
            route = "/",
        ) {
            CategoryScreen(
                topBarTitle = topBarTitle,
                maxCategoryNameLength = CategoryConstraints.MAX_CATEGORY_NAME_LENGTH,
                categoryNameInputFieldValue = categoryNameInputFieldValue,
                selectedCategoryColor = selectedCategoryColor,
                categoryList = categoryList?.map { category ->
                    CategoryListItemProp(
                        name = category.name,
                        color = category.color,
                        onClicked = if (category.name != CategoryConstraints.NON_EDITABLE_CATEGORY_NAME) { ->
                            viewModel.selectedCategory.value = category
                            showCategoryManagementBar = true
                        } else null,
                    )
                },
                onCategoryNameInputFieldValueChanged = {
                    if (it.length <= CategoryConstraints.MAX_CATEGORY_NAME_LENGTH) {
                        viewModel.newCategoryNameInputFieldValue.value = it
                    }
                },
                onCategoryColorClicked = {
                    viewModel.newCategoryColorSelectedValue.value = it
                },
                onDoneButtonClicked = {
                    viewModel.runWithScope {
                        showLoading = true

                        runCatching { createCategory() }
                            .onSuccess {
                                viewModel.newCategoryNameInputFieldValue.value = ""
                                viewModel.newCategoryColorSelectedValue.value =
                                    CategoryColor.entries.first()
                            }
                            .onFailure { e ->
                                val toastMessage = if (e is IllegalArgumentException) {
                                    "\"일상\"이라는 이름은 사용할 수 없습니다!"
                                } else if (e is IllegalStateException) {
                                    "카테고리 이름을 입력해주세요!"
                                } else {
                                    "카테고리 생성에 실패했습니다."
                                }

                                context.showToast(toastMessage)
                            }

                        showLoading = false
                    }

                    keyboard?.hide()
                },
                onBackButtonClicked = onBackButtonClicked,
            )
        }

        composable(
            route = "/modify",
        ) {
            val categoryName by viewModel.modifyingCategoryNameInputFieldValue.collectAsState()
            val categoryColor by viewModel.modifyingCategoryColorSelectedValue.collectAsState()

            BackHandler {
                MainScope().launch { navigator.popBackStack() }
            }

            CategoryModificationScreen(
                topBarTitle = topBarTitle,
                maxCategoryNameLength = CategoryConstraints.MAX_CATEGORY_NAME_LENGTH,
                categoryNameInputFieldValue = categoryName,
                selectedCategoryColor = categoryColor,
                isDoneButtonEnabled = categoryName.isNotBlank() && categoryName.length <= CategoryConstraints.MAX_CATEGORY_NAME_LENGTH,
                onCategoryNameInputFieldValueChanged = {
                    if (it.length <= CategoryConstraints.MAX_CATEGORY_NAME_LENGTH) {
                        viewModel.modifyingCategoryNameInputFieldValue.value = it
                    }
                },
                onCategoryColorClicked = {
                    viewModel.modifyingCategoryColorSelectedValue.value = it
                },
                onDoneButtonClicked = onDoneButtonClicked@{
                    viewModel.runWithScope {
                        showLoading = true

                        runCatching { modifyCategory() }
                            .onSuccess {
                                MainScope().launch { navigator.popBackStack() }
                            }
                            .onFailure { e ->
                                val toastMessage = if (e is IllegalArgumentException) {
                                    "\"일상\"이라는 이름은 사용할 수 없습니다!"
                                } else {
                                    "카테고리 수정에 실패했습니다."
                                }

                                context.showToast(toastMessage)
                            }

                        showLoading = false
                    }
                },
                onBackButtonClicked = {
                    MainScope().launch { navigator.popBackStack() }
                },
            )
        }
    }

    selectedCategory?.let { category ->
        if (showCategoryManagementBar) {
            CategoryManagementBar(
                prop = CategoryManagementBarProp(
                    onDismissed = {
                        viewModel.selectedCategory.value = null
                        showCategoryManagementBar = false
                    },
                    onModifyOptionClicked = {
                        showCategoryManagementBar = false
                        viewModel.modifyingCategoryNameInputFieldValue.value = category.name
                        viewModel.modifyingCategoryColorSelectedValue.value =
                            category.color ?: CategoryColor.entries.first()
                        MainScope().launch {
                            navigator.navigate("/modify")
                        }
                    },
                    onDeleteCategoryOptionClicked = {
                        showCategoryManagementBar = false
                        showCategoryDeletionDialog = true
                    },
                    onDeleteCategoryAndAllIncludedDiariesOptionClicked = {
                        showCategoryManagementBar = false
                        showCategoryAndAllIncludedDiaryDeletionDialog = true
                    },
                )
            )
        }

        if (showCategoryDeletionDialog) {
            CategoryDeletionDialog(
                prop = CategoryDeletionDialogProp(
                    onDismissed = {
                        showCategoryDeletionDialog = false
                    },
                    onConfirmed = {
                        viewModel.runWithScope {
                            showLoading = true

                            runCatching { deleteCategory() }.onFailure {
                                context.showToast("카테고리 삭제에 실패했습니다.")
                            }

                            showCategoryDeletionDialog = false
                            showLoading = false
                        }
                    },
                )
            )
        }

        if (showCategoryAndAllIncludedDiaryDeletionDialog) {
            CategoryAndAllIncludedDiaryDeletionDialog(
                prop = CategoryAndAllIncludedDiaryDeletionDialogProp(
                    onDismissed = {
                        showCategoryAndAllIncludedDiaryDeletionDialog = false
                    },
                    onConfirmed = {
                        viewModel.runWithScope {
                            showLoading = true

                            runCatching {
                                deleteCategoryAndAllIncludedDiaries()
                            }.onFailure {
                                context.showToast("카테고리 삭제에 실패했습니다.")
                            }

                            showCategoryAndAllIncludedDiaryDeletionDialog = false
                            showLoading = false
                        }
                    },
                )
            )
        }
    }

    if (showLoading) LoadingModal()
}