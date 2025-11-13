package tn.esprit.dam_android.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import tn.esprit.dam_android.navigation.SetupNavGraph
import tn.esprit.dam_android.navigation.screensWithBottomNav
import tn.esprit.dam_android.ui.components.SGBottomNavigationBar

@Composable
fun MainScaffold(
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in screensWithBottomNav

    Scaffold(
        bottomBar = {

            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                SGBottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SetupNavGraph(
                navController = navController,
            )
        }
    }
}