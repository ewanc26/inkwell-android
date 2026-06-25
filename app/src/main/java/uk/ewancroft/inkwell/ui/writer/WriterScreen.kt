/**
 * Post composition screen with Markdown editor.
 *
 * Mirrors Inkwell iOS ComposeView: select a publication, write a title and
 * description, compose in Markdown (with future Leaflet block support),
 * and publish to the AT Protocol via the user's PDS.
 *
 * Currently a placeholder — the publish flow and publication selector
 * will connect to a ViewModel backed by the user's site.standard.publication
 * records and the AtProtoClient.
 */
package uk.ewancroft.inkwell.ui.writer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriterScreen() {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var markdown by remember { mutableStateOf("") }
    var selectedPublication by remember { mutableIntStateOf(0) }
    var isPublishing by remember { mutableStateOf(false) }

    // Future: load from ViewModel via user's site.standard.publication records
    val publications = remember { listOf("Select a publication...") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Write") })
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(horizontal = 16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Publication Selector ────────────────────────────────
            OutlinedTextField(
                value = publications[selectedPublication],
                onValueChange = {},
                readOnly = true,
                label = { Text("Publication") },
                trailingIcon = { Icon(Icons.Outlined.ArrowDropDown, null) },
                modifier = Modifier.fillMaxWidth()
            )

            // ── Format Badge ───────────────────────────────────────
            Text("Format: Leaflet", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            // ── Title & Description ────────────────────────────────
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Title") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Description (optional)") }, maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            // ── Content Editor ─────────────────────────────────────
            OutlinedTextField(
                value = markdown, onValueChange = { markdown = it },
                label = { Text("Content (Markdown)") },
                modifier = Modifier.fillMaxWidth().weight(1f),
                minLines = 10
            )

            // ── Publish Button ─────────────────────────────────────
            Button(
                onClick = { isPublishing = true },
                enabled = title.isNotBlank() && !isPublishing,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isPublishing) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(Modifier.width(8.dp))
                }
                Icon(Icons.Outlined.Send, null)
                Spacer(Modifier.width(8.dp))
                Text("Publish")
            }
        }
    }
}
