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
