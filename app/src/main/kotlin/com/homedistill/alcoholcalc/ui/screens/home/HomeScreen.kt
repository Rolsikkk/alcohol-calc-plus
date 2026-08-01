package com.homedistill.alcoholcalc.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.homedistill.alcoholcalc.BuildConfig
import com.homedistill.alcoholcalc.R
import com.homedistill.alcoholcalc.data.UserPreferencesRepository
import com.homedistill.alcoholcalc.ui.components.rememberPressScale
import com.homedistill.alcoholcalc.ui.navigation.CalculatorTab
import com.homedistill.alcoholcalc.ui.theme.AppFieldColors

private const val STAGGER_STEP_MS = 35
private const val ITEM_ANIM_MS = 260
private const val PRESS_SCALE = 0.97f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenTab: (CalculatorTab) -> Unit,
    onOpenSettings: () -> Unit,
) {
    val context = LocalContext.current.applicationContext
    val viewModel: HomeViewModel = viewModel(
        factory = viewModelFactory {
            initializer { HomeViewModel(UserPreferencesRepository(context)) }
        },
    )
    val visibleTabs by viewModel.visibleTabs.collectAsStateWithLifecycle()

    var entranceStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entranceStarted = true }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppFieldColors.TopBarBackground,
                    titleContentColor = AppFieldColors.TopBarContent,
                ),
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(visibleTabs) { index, tab ->
                    StaggeredEntry(visible = entranceStarted, index = index) {
                        HomeListItem(
                            icon = tab.icon,
                            title = stringResource(tab.titleRes),
                            onClick = { onOpenTab(tab) },
                        )
                        HorizontalDivider()
                    }
                }
                item {
                    StaggeredEntry(visible = entranceStarted, index = visibleTabs.size) {
                        HomeListItem(
                            icon = Icons.Filled.Settings,
                            title = stringResource(R.string.tab_settings),
                            onClick = onOpenSettings,
                        )
                        HorizontalDivider()
                    }
                }
            }
            Text(
                text = stringResource(R.string.app_version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }
    }
}

/** Fades and slides an item in, delayed a little more for each successive index. */
@Composable
private fun StaggeredEntry(visible: Boolean, index: Int, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(ITEM_ANIM_MS, delayMillis = index * STAGGER_STEP_MS)) +
            slideInVertically(tween(ITEM_ANIM_MS, delayMillis = index * STAGGER_STEP_MS)) { it / 3 },
    ) {
        Column(content = { content() })
    }
}

@Composable
private fun HomeListItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource, pressedScale = PRESS_SCALE)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(26.dp),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
    }
}
