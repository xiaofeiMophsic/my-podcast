package com.example.podcastapp.core.ui.neo

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.podcastapp.core.ui.theme.PodcastTheme
import com.example.podcastapp.core.ui.R


@Composable
fun NeoBottomNav(
    items: List<NeoNavItem>,
    selectedItemId: String,
    onItemClick: (NeoNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = NeoColors.NavBg, // 假设外部定义了你的颜色
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp) // 稍微收紧一点上下间距，留给图标稍微大一点的空间
                .navigationBarsPadding(),
            // 🌟 优化：不再需要 SpaceEvenly，因为下面子元素用了 weight(1f) 将自动铺满并平分
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = item.id == selectedItemId
                NeoBottomNavItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onItemClick(item) },
                    // 🌟 核心修复：占据平分的屏幕宽度，极大增加可点击的热区！
                    modifier = Modifier.weight(1f)
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

    // 🌟 质感提升：让选中与未选中的颜色切换拥有丝滑的渐变动画
    val animatedBgColor by animateColorAsState(
        targetValue = if (isSelected) NeoColors.AccentGreen else Color.Transparent,
        animationSpec = tween(150),
        label = "navBgColor"
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) NeoColors.NavBorderSelected else NeoColors.NavBorder,
        animationSpec = tween(150),
        label = "navBorderColor"
    )

    Column(
        modifier = modifier
            // 🌟 语义化提升：使用 selectable 替代 clickable
            .selectable(
                selected = isSelected,
                interactionSource = interactionSource,
                indication = null, // 依然保留无水波纹的设定
                onClick = onClick,
                role = Role.Tab // 告诉系统这是一个底部导航 Tab
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp) // 图标和文字间距微调
    ) {
        Box(
            modifier = Modifier
                .size(52.dp) // 统一 Box 的尺寸
                .background(
                    color = animatedBgColor, // 🌟 应用颜色渐变动画
                    shape = CircleShape
                )
                .border(
                    width = if (isSelected) 1.5.dp else 1.dp, // 选中时可以微微加粗边框增强视觉重量
                    color = animatedBorderColor, // 🌟 应用边框颜色渐变动画
                    shape = CircleShape
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
            fontSize = 12.sp, // 底部导航的字号通常 10-12sp 最精致，太大反而显得粗糙
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = Color.Black,
            maxLines = 1, // 防止文字多语言撑爆
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
