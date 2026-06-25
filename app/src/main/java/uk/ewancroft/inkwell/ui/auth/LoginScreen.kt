/**
 * AT Protocol authentication screen with onboarding.
 *
 * Walks the user through: what Inkwell is (three feature highlights),
 * entering their AT Protocol handle, and initiating OAuth sign-in.
 * No app passwords — the OAuth flow delegates auth to the user's PDS.
 *
 * Mirrors Inkwell iOS LoginView in both structure and copy.
 */
package uk.ewancroft.inkwell.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {}
) {
    var handle by remember { mutableStateOf("") }
    var isSigningIn by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ── Brand Header ────────────────────────────────────────────
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

        // ── Onboarding Cards ───────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FeatureRow(Icons.Outlined.Book, "Read long-form posts from any standard.site blog")
                FeatureRow(Icons.Outlined.Notifications, "Subscribe and get notified of new posts")
                FeatureRow(Icons.Outlined.Edit, "Write and publish using the standard.site lexicon")
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Sign-In Form ───────────────────────────────────────────
        OutlinedTextField(
            value = handle,
            onValueChange = { handle = it; errorMessage = null },
            label = { Text("AT Protocol handle") },
            placeholder = { Text("alice.bsky.social") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Go
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Text(
                errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { isSigningIn = true },
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

        // ── Privacy Note ───────────────────────────────────────────
        Text(
            "Your data stays in your Personal Data Server. No app password needed — OAuth only.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Single feature row in the onboarding card. */
@Composable
private fun FeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
