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

sealed class Screen(val route: String, val label: String, val icon: ImageVector, val selectedIcon: ImageVector) {
    data object Reader : Screen("reader", "Read", Icons.Outlined.Book, Icons.Filled.Book)
    data object Discover : Screen("discover", "Discover", Icons.Outlined.Explore, Icons.Filled.Explore)
    data object Writer : Screen("writer", "Write", Icons.Outlined.Edit, Icons.Filled.Edit)
}

val bottomNavItems = listOf(Screen.Reader, Screen.Discover, Screen.Writer)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InkwellNavHost(
    isAuthenticated: Boolean,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            if (isAuthenticated && currentDestination?.hierarchy?.any {
                it.route in bottomNavItems.map { s -> s.route }
            } == true) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination.hierarchy.any { it.route == screen.route }
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (selected) screen.selectedIcon else screen.icon,
                                    contentDescription = screen.label
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
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isAuthenticated) Screen.Reader.route else "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") { LoginScreen() }
            composable(Screen.Reader.route) { ReaderScreen() }
            composable(Screen.Discover.route) { DiscoverScreen() }
            composable(Screen.Writer.route) { WriterScreen() }
            composable("post/{uri}") { backStackEntry ->
                val uri = backStackEntry.arguments?.getString("uri") ?: return@composable
                PostDetailScreen(uri)
            }
        }
    }
}
