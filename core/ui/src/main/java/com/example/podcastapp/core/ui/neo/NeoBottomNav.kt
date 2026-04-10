package com.example.podcastapp.core.ui.neo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.podcastapp.core.ui.R
import com.example.podcastapp.core.ui.theme.PodcastTheme

data class NeoNavItem(
    val label: String,
    val iconResId: Int,
    val id: String
)

@Composable
fun NeoBottomNav(
    items: List<NeoNavItem>,
    selectedItemId: String,
    onItemClick: (NeoNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = NeoColors.NavBg,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = item.id == selectedItemId
                NeoBottomNavItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}

@Composable
private fun NeoBottomNavItem(
    item: NeoNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = if (isSelected) NeoColors.AccentGreen else Color.Transparent,
                    shape = CircleShape
                )
                .then(
                    if (!isSelected) {
                        Modifier.border(1.dp, NeoColors.NavBorder, CircleShape)
                    } else {
                        Modifier.border(1.dp, NeoColors.NavBorderSelected, CircleShape)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = item.iconResId),
                contentDescription = item.label,
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
        }
        Text(
            text = item.label,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NeoBottomNavPreview() {
    val items = listOf(
        NeoNavItem(stringResource(R.string.nav_home), R.drawable.nav_home, "home"),
        NeoNavItem(stringResource(R.string.nav_explore), R.drawable.nav_explore, "explore"),
        NeoNavItem(stringResource(R.string.nav_favourites), R.drawable.nav_like, "favourites"),
        NeoNavItem(stringResource(R.string.nav_search), R.drawable.nav_setting, "search")
    )
    PodcastTheme {
        Box(modifier = Modifier.background(NeoColors.ScreenBg)) {
            NeoBottomNav(
                items = items,
                selectedItemId = "home",
                onItemClick = {}
            )
        }
    }
}
