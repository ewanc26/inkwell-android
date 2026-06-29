package uk.ewancroft.inkwell.ui.writer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriterScreen(
    viewModel: WriterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadPublications()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Write") })
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(horizontal = 16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.isLoadingPublications) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.publications.isEmpty()) {
                Text("No publications found.", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Button(onClick = { viewModel.showCreateDialog() }) {
                    Icon(Icons.Outlined.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Create a Publication")
                }
            } else {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = uiState.selectedPublication?.name ?: "Select a publication...",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Publication") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        uiState.publications.forEach { pub ->
                            DropdownMenuItem(
                                text = { Text(pub.name) },
                                onClick = {
                                    viewModel.selectPublication(pub)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                TextButton(onClick = { viewModel.showCreateDialog() }) {
                    Icon(Icons.Outlined.Add, contentDescription = null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("New Publication")
                }
            }

            Text("Format: Leaflet", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            OutlinedTextField(
                value = uiState.title, onValueChange = { viewModel.onTitleChanged(it) },
                label = { Text("Title") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.description, onValueChange = { viewModel.onDescriptionChanged(it) },
                label = { Text("Description (optional)") }, maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.markdown, onValueChange = { viewModel.onMarkdownChanged(it) },
                label = { Text("Content (Markdown)") },
                modifier = Modifier.fillMaxWidth().weight(1f),
                minLines = 10
            )

            if (uiState.publishError != null) {
                Text(
                    uiState.publishError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (uiState.publishSuccess != null) {
                Text(
                    uiState.publishSuccess!!,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = { viewModel.publish() },
                enabled = uiState.title.isNotBlank() && uiState.selectedPublication != null && !uiState.isPublishing,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isPublishing) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(Modifier.width(8.dp))
                }
                Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Publish")
            }
        }

            if (uiState.showCreateDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.dismissCreateDialog() },
                    title = { Text("New Publication") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = uiState.createUrl,
                                onValueChange = { viewModel.onCreateUrlChanged(it) },
                                label = { Text("URL (e.g. https://mysite.com)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = uiState.createName,
                                onValueChange = { viewModel.onCreateNameChanged(it) },
                                label = { Text("Publication Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = uiState.createDescription,
                                onValueChange = { viewModel.onCreateDescriptionChanged(it) },
                                label = { Text("Description (optional)") },
                                maxLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (uiState.createError != null) {
                                Text(
                                    uiState.createError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.createPublication() },
                            enabled = uiState.createUrl.isNotBlank() && uiState.createName.isNotBlank() && !uiState.isCreating
                        ) {
                            if (uiState.isCreating) {
                                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary)
                                Spacer(Modifier.width(8.dp))
                            }
                            Text("Create")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.dismissCreateDialog() }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
