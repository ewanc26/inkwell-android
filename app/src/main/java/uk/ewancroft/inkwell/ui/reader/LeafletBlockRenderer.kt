package uk.ewancroft.inkwell.ui.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import uk.ewancroft.inkwell.data.model.common.AtUri
import uk.ewancroft.inkwell.data.model.common.BlobRef
import uk.ewancroft.inkwell.data.model.common.StrongRef
import uk.ewancroft.inkwell.data.model.content.LeafletBlock
import uk.ewancroft.inkwell.data.model.content.LeafletContent
import uk.ewancroft.inkwell.data.model.content.LeafletFacet
import uk.ewancroft.inkwell.data.model.content.LeafletPage
import uk.ewancroft.inkwell.data.model.content.ListItem as ListItemModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeafletBlockContent(
    block: LeafletBlock,
    authorDid: String,
    modifier: Modifier = Modifier,
) {
    when (block.type) {
        "pub.leaflet.blocks.text" -> TextBlock(block)
        "pub.leaflet.blocks.header" -> HeaderBlock(block)
        "pub.leaflet.blocks.paragraph", "pub.leaflet.blocks.blockquote" -> ParagraphBlock(block)
        "pub.leaflet.blocks.code" -> CodeBlock(block)
        "pub.leaflet.blocks.math" -> MathBlock(block)
        "pub.leaflet.blocks.image" -> ImageBlock(block, authorDid)
        "pub.leaflet.blocks.unorderedList" -> UnorderedListBlock(block)
        "pub.leaflet.blocks.orderedList" -> OrderedListBlock(block)
        "pub.leaflet.blocks.checklist" -> ChecklistBlock(block)
        "pub.leaflet.blocks.bskyPost" -> BskyPostBlock(block)
        "pub.leaflet.blocks.standardSitePost" -> StandardSitePostBlock(block)
        "pub.leaflet.blocks.website" -> WebsiteEmbedBlock(block)
        "pub.leaflet.blocks.iframe" -> IframeEmbedBlock(block)
        "pub.leaflet.blocks.button" -> ButtonBlock(block)
        "pub.leaflet.blocks.divider" -> DividerBlock()
        "pub.leaflet.blocks.page" -> PageBlock(block)
        else -> UnknownBlock(block)
    }
}

@Composable
fun TextBlock(block: LeafletBlock) {
    Text(
        block.plaintext ?: "",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun HeaderBlock(block: LeafletBlock) {
    val level = when (block.level) {
        1 -> MaterialTheme.typography.headlineSmall
        2 -> MaterialTheme.typography.headlineSmall
        3 -> MaterialTheme.typography.titleMedium
        else -> MaterialTheme.typography.titleLarge
    }
    Text(
        block.plaintext ?: "",
        style = level,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ParagraphBlock(block: LeafletBlock) {
    if (block.type == "pub.leaflet.blocks.blockquote") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.width(4.dp).height(40.dp).background(MaterialTheme.colorScheme.primary)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                block.plaintext ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    } else {
        Text(
            block.plaintext ?: "",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CodeBlock(block: LeafletBlock) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(12.dp)) {
            val language = block.language?.let { " | $it" } ?: ""
            Text(
                "Code$language",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(
                block.plaintext ?: "",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun MathBlock(block: LeafletBlock) {
    Text(
        block.tex ?: "",
        style = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp
        ),
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun ImageBlock(block: LeafletBlock, authorDid: String) {
    val imageUrl = if (block.image?.link?.startsWith("http") == true) block.image.link else "https://cdn.bsky.app/img/feed_thumbnail/plain/${authorDid}/${block.image?.link}"
    AsyncImage(
        model = imageUrl,
        contentDescription = block.alt,
        modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun UnorderedListBlock(block: LeafletBlock) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        block.children?.forEach { item -> ListItem(item) }
    }
}

@Composable
fun OrderedListBlock(block: LeafletBlock) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        block.children?.forEachIndexed { index, item -> ListItem(item, index + 1) }
    }
}

@Composable
fun ChecklistBlock(block: LeafletBlock) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        block.children?.forEach { item -> ChecklistItem(item) }
    }
}

@Composable
fun ListItem(item: ListItemModel, number: Int? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (item.type == "pub.leaflet.blocks.checklist") {
            Box(
                modifier = Modifier.size(20.dp),
                contentAlignment = Alignment.Center
            ) {
                val icon = if (item.checked == true) Icons.Outlined.CheckBox else Icons.Outlined.CheckBoxOutlineBlank
                val tint = if (item.checked == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = tint)
            }
            Spacer(Modifier.width(12.dp))
        } else if (number != null) {
            Text(
                "$number.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.width(24.dp)
            )
            Spacer(Modifier.width(8.dp))
        } else {
            Icon(
                Icons.Outlined.FiberManualRecord,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(12.dp))
        }
        if (item.content != null) {
            LeafletBlockContent(item.content, "", Modifier.weight(1f))
        }
    }
}

@Composable
fun ChecklistItem(item: ListItemModel) {
    ListItem(item)
}

@Composable
fun BskyPostBlock(block: LeafletBlock) {
    val subject = block.subject
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.FiberManualRecord, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Bluesky Post", style = MaterialTheme.typography.labelMedium)
                if (subject != null) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        subject.uri,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun StandardSitePostBlock(block: LeafletBlock) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Web, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Standard.site Post", style = MaterialTheme.typography.labelMedium)
            }
            if (block.standardSitePostSubject != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    block.standardSitePostSubject,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun WebsiteEmbedBlock(block: LeafletBlock) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(12.dp)) {
            if (block.websiteTitle != null) {
                Text(
                    block.websiteTitle,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            if (block.websiteDescription != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    block.websiteDescription,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (block.url != null) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(block.url, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun IframeEmbedBlock(block: LeafletBlock) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                "IFrame Embed: ${block.url}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ButtonBlock(block: LeafletBlock) {
    Button(
        onClick = { /* TODO: Handle button click */ },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(
            block.text ?: "",
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun DividerBlock() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@Composable
fun PageBlock(block: LeafletBlock) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.AutoMirrored.Outlined.Article, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(
                "Page ${block.pageIndex ?: 1}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun UnknownBlock(block: LeafletBlock) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                "Unsupported: ${block.type}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}