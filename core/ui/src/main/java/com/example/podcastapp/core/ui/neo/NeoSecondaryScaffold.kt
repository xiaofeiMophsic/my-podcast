package com.example.podcastapp.core.ui.neo

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier

/**
 * App-level SnackbarHostState, provided via CompositionLocalProvider in AppRoot.
 * Any screen can use `LocalSnackbarHostState.current` to show snackbars
 * without managing its own SnackbarHostState.
 */
val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("SnackbarHostState not provided — wrap with CompositionLocalProvider in AppRoot")
}

/**
 * Standard scaffold for secondary (non-top-level) pages.
 * Provides NeoTopBar with back navigation and optional SnackbarHost.
 *
 * Usage:
 * ```
 * NeoSecondaryScaffold(title = "Downloads", onBack = onBack) { padding ->
 *     LazyColumn(modifier = Modifier.padding(padding)) { ... }
 * }
 * ```
 *
 * With custom actions and local snackbar:
 * ```
 * val snackbarState = remember { SnackbarHostState() }
 * NeoSecondaryScaffold(
 *     title = "Add RSS",
 *     onBack = onBack,
 *     snackbarHostState = snackbarState,
 *     actions = { IconButton(onClick = { }) { Icon(...) } },
 * ) { padding -> ... }
 * ```
 *
 * @param title TopBar title text.
 * @param onBack Back button callback.
 * @param modifier Modifier for the Scaffold.
 * @param snackbarHostState Optional local SnackbarHostState. If null, uses [LocalSnackbarHostState].
 * @param actions Optional trailing actions in the TopBar (e.g. share, delete buttons).
 * @param content Page content receiving Scaffold padding.
 */
@Composable
fun NeoSecondaryScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val effectiveSnackbarState = snackbarHostState ?: LocalSnackbarHostState.current

    Scaffold(
        modifier = modifier,
        topBar = {
            NeoTopBar(
                title = title,
                onBack = onBack,
                action = actions,
            )
        },
        snackbarHost = { SnackbarHost(effectiveSnackbarState) },
        containerColor = NeoColors.ScreenBg,
    ) { padding ->
        content(padding)
    }
}
