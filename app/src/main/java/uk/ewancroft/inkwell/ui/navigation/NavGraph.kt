package uk.ewancroft.inkwell.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import uk.ewancroft.inkwell.ui.auth.LoginScreen
import uk.ewancroft.inkwell.ui.reader.ReaderScreen
import uk.ewancroft.inkwell.ui.reader.PostDetailScreen
import uk.ewancroft.inkwell.ui.writer.WriterScreen
import uk.ewancroft.inkwell.ui.discover.DiscoverScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String, val label: String, val icon: ImageVector, val selectedIcon: ImageVector) {
    data object Reader   : Screen("reader",   "Read",     Icons.Outlined.Book,    Icons.Filled.Book)
    data object Discover : Screen("discover", "Discover", Icons.Outlined.Explore, Icons.Filled.Explore)
    data object Writer   : Screen("writer",   "Write",    Icons.Outlined.Edit,    Icons.Filled.Edit)
}

val bottomNavItems = listOf(Screen.Reader, Screen.Discover, Screen.Writer)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InkwellNavHost(
    isAuthenticated: Boolean,
    onSignOut: () -> Unit = {},
    navController: NavHostController = rememberNavController(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Show the bottom nav only on the three root tabs — hide it on post detail.
    val showBottomBar = isAuthenticated && currentDestination?.hierarchy?.any { dest ->
        bottomNavItems.any { it.route == dest.route }
    } == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (selected) screen.selectedIcon else screen.icon,
                                    contentDescription = screen.label,
                                )
                            },
                            label = { Text(screen.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isAuthenticated) Screen.Reader.route else "login",
            modifier = Modifier.padding(innerPadding),
        ) {
            composable("login") {
                LoginScreen()
            }

            composable(Screen.Reader.route) {
                ReaderScreen(
                    onNavigateToPost = { uri ->
                        // AT URIs contain slashes — URL-encode before embedding in the path.
                        val encoded = URLEncoder.encode(uri, StandardCharsets.UTF_8.name())
                        navController.navigate("post/$encoded")
                    },
                    onSignOut = onSignOut,
                )
            }

            composable(Screen.Discover.route) {
                DiscoverScreen()
            }

            composable(Screen.Writer.route) {
                WriterScreen(onSignOut = onSignOut)
            }

            composable("post/{uri}") { backStackEntry ->
                val encoded = backStackEntry.arguments?.getString("uri") ?: return@composable
                val uri = URLDecoder.decode(encoded, StandardCharsets.UTF_8.name())
                PostDetailScreen(
                    uri = uri,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
