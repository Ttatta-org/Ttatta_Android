package com.umc.category

import android.widget.Toast
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
import com.umc.category.modal.CategoryAndAllIncludedDiaryDeletionDialogProp
import com.umc.category.modal.CategoryDeletionDialog
import com.umc.category.modal.CategoryDeletionDialogProp
import com.umc.category.modal.CategoryManagementBar
import com.umc.category.modal.CategoryManagementBarProp
import com.umc.category.screen.CategoryListItemProp
import com.umc.category.screen.CategoryModificationScreen
import com.umc.category.screen.CategoryScreen
import com.umc.core.model.CategoryInfo
import com.umc.core.util.runWithScope
import com.umc.design.CategoryColor
import com.umc.design.component.LoadingModal
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

const val MAX_CATEGORY_NAME_LENGTH = 20
const val NON_EDITABLE_CATEGORY_NAME = "일상"

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

    var categoryNameInputFieldValue by remember { mutableStateOf("") }
    var selectedCategoryColor by remember { mutableStateOf(CategoryColor.entries.first()) }
    var selectedCategory by remember { mutableStateOf<CategoryInfo?>(null) }
    var showCategoryManagementBar by remember { mutableStateOf(false) }
    var showCategoryDeletionDialog by remember { mutableStateOf(false) }
    var showCategoryAndAllIncludedDiaryDeletionDialog by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.runWithScope {
            runCatching { getCategoryListFromServer() }
                .onFailure {
                    val toast = Toast.makeText(
                        context,
                        "카테고리 목록을 불러오는데 실패했습니다.",
                        Toast.LENGTH_SHORT,
                    )

                    toast.show()
                }
        }
    }

    NavHost(
        navController = navigator,
        startDestination = "/",
    ) {
        composable(
            route = "/"
        ) {
            CategoryScreen(
                topBarTitle = topBarTitle,
                maxCategoryNameLength = MAX_CATEGORY_NAME_LENGTH,
                categoryNameInputFieldValue = categoryNameInputFieldValue,
                selectedCategoryColor = selectedCategoryColor,
                categoryList = categoryList?.map { category ->
                    CategoryListItemProp(
                        name = category.name,
                        color = category.color,
                        onClicked = if (category.name != NON_EDITABLE_CATEGORY_NAME) { ->
                            selectedCategory = category
                            showCategoryManagementBar = true
                        } else null,
                    )
                },
                onCategoryNameInputFieldValueChanged = {
                    if (it.length <= MAX_CATEGORY_NAME_LENGTH) categoryNameInputFieldValue = it
                },
                onCategoryColorClicked = {
                    selectedCategoryColor = it
                },
                onDoneButtonClicked = {
                    viewModel.runWithScope {
                        showLoading = true

                        runCatching {
                            createCategory(
                                name = categoryNameInputFieldValue,
                                color = selectedCategoryColor,
                            )
                        }
                            .onSuccess {
                                categoryNameInputFieldValue = ""
                                selectedCategoryColor = CategoryColor.entries.first()
                            }
                            .onFailure {
                                val toast =
                                    Toast.makeText(context, "카테고리 생성에 실패했습니다.", Toast.LENGTH_SHORT)
                                toast.show()
                            }

                        showLoading = false
                    }

                    keyboard?.hide()
                },
                onBackButtonClicked = onBackButtonClicked,
            )
        }

        composable(
            route = "/modify"
        ) {
            var categoryName by remember(selectedCategory) {
                mutableStateOf(selectedCategory?.name ?: "")
            }

            var categoryColor by remember(selectedCategory) {
                mutableStateOf(selectedCategory?.color ?: CategoryColor.entries.first())
            }

            BackHandler {
                MainScope().launch { navigator.popBackStack() }
            }

            CategoryModificationScreen(
                topBarTitle = topBarTitle,
                maxCategoryNameLength = MAX_CATEGORY_NAME_LENGTH,
                categoryNameInputFieldValue = categoryName,
                selectedCategoryColor = categoryColor,
                isDoneButtonEnabled = categoryName.isNotBlank() && categoryName.length <= MAX_CATEGORY_NAME_LENGTH,
                onCategoryNameInputFieldValueChanged = {
                    if (it.length <= MAX_CATEGORY_NAME_LENGTH) categoryName = it
                },
                onCategoryColorClicked = { categoryColor = it },
                onDoneButtonClicked = onDoneButtonClicked@{
                    val id = selectedCategory?.id ?: return@onDoneButtonClicked
                    val categoryName = categoryName.trim()

                    if (categoryName == "일상") {
                        MainScope().launch {
                            val toast = Toast.makeText(
                                context,
                                "\"일상\"이라는 이름은 사용할 수 없습니다!",
                                Toast.LENGTH_SHORT,
                            )

                            toast.show()
                        }

                        return@onDoneButtonClicked
                    }

                    viewModel.runWithScope {
                        showLoading = true

                        runCatching {
                            modifyCategory(
                                id = id,
                                name = categoryName,
                                color = categoryColor,
                            )
                        }
                            .onSuccess {
                                MainScope().launch { navigator.popBackStack() }
                            }
                            .onFailure {
                                val toast = Toast.makeText(
                                    context,
                                    "카테고리 수정에 실패했습니다.",
                                    Toast.LENGTH_SHORT,
                                )

                                toast.show()
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
                        selectedCategory = null
                        showCategoryManagementBar = false
                    },
                    onModifyOptionClicked = {
                        showCategoryManagementBar = false
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

                            runCatching { deleteCategory(id = category.id) }.onFailure {
                                val toast = Toast.makeText(
                                    context,
                                    "카테고리 삭제에 실패했습니다.",
                                    Toast.LENGTH_SHORT,
                                )

                                toast.show()
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
                                deleteCategoryAndAllIncludedDiaries(id = category.id)
                            }.onFailure {
                                val toast = Toast.makeText(
                                    context,
                                    "카테고리 삭제에 실패했습니다.",
                                    Toast.LENGTH_SHORT,
                                )

                                toast.show()
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