/**
 * Publication discovery and search screen.
 *
 * Placeholder for the standard.site publication directory — in the full
 * implementation this will search the AT Protocol network for publications
 * and provide curated discovery feeds. Mirrors Inkwell iOS DiscoverView.
 */
package uk.ewancroft.inkwell.ui.discover

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text("Discover") }) }) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Outlined.Explore, null, Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            Text("Discover Publications", style = MaterialTheme.typography.titleMedium)
            Text(
                "Search for standard.site publications across the AT Protocol network.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
