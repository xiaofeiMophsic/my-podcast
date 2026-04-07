package com.example.podcastapp.core.ui.neo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShadowCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.padding(end = 4.dp, bottom = 4.dp)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 4.dp, y = 4.dp)
                .clip(NeoShapes.Card)
                .background(NeoColors.Shadow),
        )
        Surface(
            shape = NeoShapes.Card,
            color = NeoColors.CardBg,
            border = BorderStroke(0.5.dp, NeoColors.CardBorder),
            content = content,
        )
    }
}

@Composable
fun NeoTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = NeoColors.Ink,
                border = BorderStroke(0.75.dp, NeoColors.NavBorder),
                onClick = onBack,
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    Text("←", color = NeoColors.CardBg, fontSize = 16.sp)
                }
            }
        } else {
            Spacer(modifier = Modifier.size(36.dp))
        }

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = NeoColors.TextPrimary,
        )

        if (action != null) {
            action()
        } else {
            Spacer(modifier = Modifier.width(36.dp))
        }
    }
}

@Composable
fun NeoPrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val bg = if (enabled) NeoColors.Ink else NeoColors.NavBorder
    Surface(
        modifier = modifier
            .height(44.dp)
            .clickable(enabled = enabled, onClick = onClick),
        shape = NeoShapes.Card,
        color = bg,
        border = BorderStroke(1.dp, NeoColors.CardBorder),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            Text(text = text, color = NeoColors.CardBg, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun NeoOutlineButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val border = if (enabled) NeoColors.CardBorder else NeoColors.NavBorder
    Surface(
        modifier = modifier
            .height(44.dp)
            .clickable(enabled = enabled, onClick = onClick),
        shape = NeoShapes.Card,
        color = NeoColors.CardBg,
        border = BorderStroke(1.dp, border),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            Text(text = text, color = NeoColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun NeoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    inputModifier: Modifier = Modifier,
) {
    ShadowCard(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (value.isBlank()) {
                Text(text = placeholder, color = NeoColors.TextSecondary, fontSize = 14.sp)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                textStyle = TextStyle(
                    color = if (enabled) NeoColors.TextPrimary else NeoColors.TextSecondary,
                    fontSize = 14.sp,
                ),
                modifier = inputModifier.fillMaxWidth(),
            )
        }
    }
}
