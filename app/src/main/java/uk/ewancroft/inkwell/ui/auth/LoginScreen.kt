package uk.ewancroft.inkwell.ui.auth

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var handle by remember { mutableStateOf("") }
    var isSigningIn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.authUrl.collect { url ->
            isSigningIn = true
            val intent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
            intent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.launchUrl(context, Uri.parse(url))
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.LoggedOut) {
            isSigningIn = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.Book,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))
        Text("Inkwell", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Read and write on the AT Protocol",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FeatureRow(Icons.Outlined.Book, "Read long-form posts from any standard.site blog — directly from the AT Protocol network, no middleman.", Color(0xFF2E7DD1))
                FeatureRow(Icons.Outlined.Notifications, "Subscribe to publications, leave comments, and recommend posts. Your data stays in your PDS.", Color(0xFFE8A040))
                FeatureRow(Icons.Outlined.Edit, "Write and publish your own posts using the standard.site lexicon.", MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = handle,
            onValueChange = { handle = it },
            label = { Text("AT Protocol handle") },
            placeholder = { Text("alice.bsky.social") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Go
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                isSigningIn = true
                viewModel.beginLogin(handle)
            },
            enabled = handle.isNotBlank() && !isSigningIn,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSigningIn) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
            }
            Text("Sign in with your PDS")
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "Your data stays in your Personal Data Server. No app password needed — OAuth only.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, iconTint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
