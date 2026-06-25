/**
 * Main activity hosting the Jetpack Compose UI tree.
 *
 * Sets up edge-to-edge rendering, wraps content in the Inkwell theme,
 * and hands navigation off to InkwellNavHost. The Hilt @AndroidEntryPoint
 * annotation ensures the Dagger graph is available to child composables.
 *
 * Authentication gate: isAuthenticated is hard-coded to false for now,
 * matching the iOS pre-auth flow where LoginScreen is the start destination.
 */
package uk.ewancroft.inkwell

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import uk.ewancroft.inkwell.ui.navigation.InkwellNavHost
import uk.ewancroft.inkwell.ui.theme.InkwellTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InkwellTheme {
                InkwellNavHost(isAuthenticated = false)
            }
        }
    }
}
