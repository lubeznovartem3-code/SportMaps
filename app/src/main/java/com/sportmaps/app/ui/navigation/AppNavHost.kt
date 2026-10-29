package com.sportmaps.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sportmaps.app.data.AppContainer
import com.sportmaps.app.ui.screens.*
import com.sportmaps.app.ui.viewmodel.*

/**
 * Граф навигации по схеме User Flow:
 * аккаунт есть? → вход / регистрация → карта → площадка → «Играть» → сбор состава →
 * друзья → чат; боковое меню → профиль / друзья / настройки.
 */
@Composable
fun AppNavHost(container: AppContainer) {
    val nav = rememberNavController()
    val start = if (container.auth.currentUser.value != null) Routes.MAP else Routes.LOGIN

    NavHost(navController = nav, startDestination = start) {

        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = appViewModel<AuthViewModel>(),
                onSuccess = { nav.toMapClearingStack() },
                onGoToLogin = {
                    nav.navigate(Routes.LOGIN) { popUpTo(Routes.REGISTER) { inclusive = true } }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = appViewModel<AuthViewModel>(),
                onSuccess = { nav.toMapClearingStack() },
                onGoToRegister = {
                    nav.navigate(Routes.REGISTER) { popUpTo(Routes.LOGIN) { inclusive = true } }
                }
            )
        }

        composable(Routes.MAP) {
            MapScreen(
                viewModel = appViewModel<MapViewModel>(),
                sessionViewModel = appViewModel<SessionViewModel>(),
                onPlay = { nav.navigate(Routes.LOBBY) { launchSingleTop = true } },
                onNavigate = { route -> nav.navigateFromDrawer(route) }
            )
        }

        composable(Routes.LOBBY) {
            LobbyScreen(
                viewModel = appViewModel<LobbyViewModel>(),
                onBack = { nav.popBackStack() },
                onInvite = { nav.navigate(Routes.friends(true)) { launchSingleTop = true } },
                onLeft = { nav.popBackStack(Routes.MAP, false) }
            )
        }

        composable(
            route = Routes.FRIENDS,
            arguments = listOf(navArgument("invite") { type = NavType.BoolType })
        ) { entry ->
            val invite = entry.arguments?.getBoolean("invite") ?: false
            FriendsScreen(
                viewModel = appViewModel<FriendsViewModel>(),
                sessionViewModel = appViewModel<SessionViewModel>(),
                inviteMode = invite,
                onBack = { nav.popBackStack() },
                onOpenChat = { id -> nav.navigate(Routes.chat(id)) },
                onNavigate = { route -> nav.navigateFromDrawer(route) }
            )
        }

        composable(
            route = Routes.CHAT,
            arguments = listOf(navArgument("friendId") { type = NavType.StringType })
        ) { entry ->
            ChatScreen(
                viewModel = appViewModel<ChatViewModel>(),
                friendId = entry.arguments?.getString("friendId").orEmpty(),
                onBack = { nav.popBackStack() }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                viewModel = appViewModel<ProfileViewModel>(),
                onBack = { nav.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                viewModel = appViewModel<SettingsViewModel>(),
                onBack = { nav.popBackStack() },
                onSaved = { nav.popBackStack(Routes.MAP, false) },
                onLogout = {
                    nav.navigate(Routes.LOGIN) { popUpTo(nav.graph.id) { inclusive = true } }
                }
            )
        }
    }
}

/** Переход на карту с очисткой стека (после входа/регистрации). */
private fun NavHostController.toMapClearingStack() {
    navigate(Routes.MAP) { popUpTo(graph.id) { inclusive = true } }
}

/** Переход из бокового меню. */
private fun NavHostController.navigateFromDrawer(route: String) {
    if (route == Routes.MAP) {
        popBackStack(Routes.MAP, false)
    } else {
        navigate(route) { launchSingleTop = true }
    }
}
