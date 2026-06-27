package uk.ewancroft.inkwell.ui.writer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import uk.ewancroft.inkwell.data.model.common.AtUri
import uk.ewancroft.inkwell.data.repository.PdsRepository
import javax.inject.Inject

data class PublicationItem(
    val uri: String,
    val name: String,
    val did: String
)

data class WriterUiState(
    val publications: List<PublicationItem> = emptyList(),
    val selectedPublication: PublicationItem? = null,
    val title: String = "",
    val description: String = "",
    val markdown: String = "",
    val isPublishing: Boolean = false,
    val publishError: String? = null,
    val publishSuccess: String? = null,
    val isLoadingPublications: Boolean = false
)

@HiltViewModel
class WriterViewModel @Inject constructor(
    private val pdsRepository: PdsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WriterUiState())
    val uiState: StateFlow<WriterUiState> = _uiState.asStateFlow()

    fun loadPublications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingPublications = true)
            try {
                val session = pdsRepository.getSession() ?: return@launch
                val response = pdsRepository.listRecords(
                    did = session.did,
                    collection = "site.standard.publication",
                    pdsUrl = session.pdsUrl
                )
                val records = response["records"]?.jsonArray.orEmpty()
                val pubs = records.mapNotNull { record ->
                    try {
                        val obj = record.jsonObject
                        val uri = obj["uri"]?.jsonPrimitive?.content ?: return@mapNotNull null
                        val value = obj["value"]?.jsonObject ?: return@mapNotNull null
                        val name = value["name"]?.jsonPrimitive?.content ?: "Unnamed"
                        val parsed = AtUri.parse(uri)
                        PublicationItem(uri, name, parsed?.did ?: session.did)
                    } catch (_: Exception) { null }
                }
                _uiState.value = _uiState.value.copy(
                    publications = pubs,
                    selectedPublication = pubs.firstOrNull(),
                    isLoadingPublications = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingPublications = false,
                    publishError = "Failed to load publications: ${e.message}"
                )
            }
        }
    }

    fun selectPublication(publication: PublicationItem) {
        _uiState.value = _uiState.value.copy(selectedPublication = publication)
    }

    fun onTitleChanged(title: String) {
        _uiState.value = _uiState.value.copy(title = title, publishError = null, publishSuccess = null)
    }

    fun onDescriptionChanged(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun onMarkdownChanged(markdown: String) {
        _uiState.value = _uiState.value.copy(markdown = markdown)
    }

    fun publish() {
        val state = _uiState.value
        val pub = state.selectedPublication ?: return

        if (state.title.isBlank()) {
            _uiState.value = state.copy(publishError = "Title is required")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isPublishing = true, publishError = null, publishSuccess = null)
            try {
                val now = java.time.Instant.now().toString()

                val record = buildJsonObject {
                    put("\$type", "site.standard.document")
                    put("site", pub.uri)
                    put("title", state.title.trim())
                    put("publishedAt", now)
                    if (state.description.isNotBlank()) {
                        put("description", state.description.trim())
                    }
                    if (state.markdown.isNotBlank()) {
                        put("textContent", state.markdown)
                    }
                }

                val result = pdsRepository.createRecord(
                    collection = "site.standard.document",
                    record = record,
                )

                _uiState.value = _uiState.value.copy(
                    isPublishing = false,
                    publishSuccess = "Published: ${result["uri"]?.jsonPrimitive?.content}",
                    title = "",
                    description = "",
                    markdown = ""
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isPublishing = false,
                    publishError = "Failed to publish: ${e.message}"
                )
            }
        }
    }
}
