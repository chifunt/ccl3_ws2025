package com.chifunt.chromaticharptabs.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.chifunt.chromaticharptabs.ui.screens.LibraryScreen
import com.chifunt.chromaticharptabs.ui.screens.OnboardingScreen
import com.chifunt.chromaticharptabs.ui.screens.PracticeScreen
import com.chifunt.chromaticharptabs.ui.screens.SettingsScreen
import com.chifunt.chromaticharptabs.ui.screens.TabDetailScreen
import com.chifunt.chromaticharptabs.ui.screens.TabEditorScreen
import com.chifunt.chromaticharptabs.ui.screens.VirtualHarmonicaScreen

enum class Routes(val route: String) {
    Library(ROUTE_LIBRARY),
    Detail(ROUTE_DETAIL),
    Editor(ROUTE_EDITOR),
    Practice(ROUTE_PRACTICE),
    Settings(ROUTE_SETTINGS),
    Harmonica(ROUTE_HARMONICA),
    Onboarding(ROUTE_ONBOARDING)
}

@Composable
fun ChromaticHarpTabsApp(
    modifier: Modifier = Modifier,
    startDestination: String = Routes.Library.route,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.Library.route) {
            LibraryScreen(
                onTabClick = { id -> navController.navigate(detailRoute(id)) },
                onCreateNew = { navController.navigate(editorRoute(null)) },
                onSettings = { navController.navigate(ROUTE_SETTINGS) },
                onHarmonica = { navController.navigate(ROUTE_HARMONICA) }
            )
        }
        composable(
            Routes.Detail.route,
            listOf(navArgument(NAV_ARG_TAB_ID) { type = NavType.IntType })
        ) {
            TabDetailScreen(
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(editorRoute(id)) },
                onPractice = { id -> navController.navigate(practiceRoute(id)) }
            )
        }
        composable(
            Routes.Editor.route,
            listOf(navArgument(NAV_ARG_TAB_ID) {
                type = NavType.IntType
                defaultValue = -1
            })
        ) {
            TabEditorScreen(
                onCancel = { navController.popBackStack() },
                onSaved = { id ->
                    navController.navigate(detailRoute(id)) {
                        popUpTo(Routes.Library.route)
                    }
                }
            )
        }
        composable(
            Routes.Practice.route,
            listOf(navArgument(NAV_ARG_TAB_ID) { type = NavType.IntType })
        ) {
            PracticeScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onViewOnboarding = { navController.navigate(ROUTE_ONBOARDING) }
            )
        }
        composable(Routes.Harmonica.route) {
            VirtualHarmonicaScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Routes.Library.route, navOptions {
                        popUpTo(Routes.Onboarding.route) { inclusive = true }
                    })
                }
            )
        }
    }
}
