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

    val publications = remember { listOf("Select a publication...") } // Load from ViewModel

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Write") })
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(horizontal = 16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Publication selector
            OutlinedTextField(
                value = publications[selectedPublication],
                onValueChange = {},
                readOnly = true,
                label = { Text("Publication") },
                trailingIcon = { Icon(Icons.Outlined.ArrowDropDown, null) },
                modifier = Modifier.fillMaxWidth()
            )

            // Format picker placeholder
            Text("Format: Leaflet", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

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

            // Markdown editor
            OutlinedTextField(
                value = markdown, onValueChange = { markdown = it },
                label = { Text("Content (Markdown)") },
                modifier = Modifier.fillMaxWidth().weight(1f),
                minLines = 10
            )

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
