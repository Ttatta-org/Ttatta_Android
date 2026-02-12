package com.umc.mypage.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object NavigationUtil {

    @Composable
    fun NavController.getSafeBackNavigatorCallback(backStackEntry: NavBackStackEntry): () -> Unit {
        val currentBackStackEntry by currentBackStackEntryAsState()

        return remember(currentBackStackEntry, backStackEntry) {
            {
                MainScope().launch {
                    if (currentBackStackEntry == backStackEntry) {
                        popBackStack()
                    }
                }
            }
        }
    }

    @Composable
    fun NavController.getSafeNavigatorCallback(
        backStackEntry: NavBackStackEntry,
        route: String,
    ): () -> Unit {
        val currentBackStackEntry by currentBackStackEntryAsState()

        return remember(currentBackStackEntry, backStackEntry) {
            {
                MainScope().launch {
                    if (currentBackStackEntry == backStackEntry) {
                        navigate(route)
                    }
                }
            }
        }
    }
}