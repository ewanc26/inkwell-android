package uk.ewancroft.inkwell

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import uk.ewancroft.inkwell.ui.auth.AuthUiState
import uk.ewancroft.inkwell.ui.auth.AuthViewModel
import uk.ewancroft.inkwell.ui.navigation.InkwellNavHost
import uk.ewancroft.inkwell.ui.theme.InkwellTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var authViewModel: AuthViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: AuthViewModel = hiltViewModel()
            authViewModel = viewModel

            val authState by viewModel.uiState.collectAsStateWithLifecycle()
            val isAuthenticated = authState is AuthUiState.LoggedIn

            InkwellTheme {
                InkwellNavHost(isAuthenticated = isAuthenticated)
            }
        }

        handleOAuthRedirect(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleOAuthRedirect(intent)
    }

    private fun handleOAuthRedirect(intent: Intent?) {
        val data = intent?.data ?: return
        if (data.scheme == "uk.ewancroft.inkwell" && data.path?.startsWith("/callback") == true) {
            authViewModel?.completeLogin(data.toString())
        }
    }
}
