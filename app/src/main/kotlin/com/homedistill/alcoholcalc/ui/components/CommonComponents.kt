package com.homedistill.alcoholcalc.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homedistill.alcoholcalc.ui.theme.AppFieldColors

const val DASH = "—"

/** Animated scale factor (1f normally, [pressedScale] while pressed) for tactile button feedback. */
@Composable
fun rememberPressScale(interactionSource: MutableInteractionSource, pressedScale: Float = 0.95f): Float {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) pressedScale else 1f, label = "pressScale")
    return scale
}

/** Screen scaffold shared by every calculator: dark top bar with a back button, scrollable content. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScaffold(
    title: String,
    onBack: () -> Unit,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppFieldColors.TopBarBackground,
                    titleContentColor = AppFieldColors.TopBarContent,
                    navigationIconContentColor = AppFieldColors.TopBarContent,
                ),
            )
        },
        bottomBar = bottomBar,
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content,
        )
    }
}

/** Flat bordered card (no elevation) matching the reference app's plain box look. */
@Composable
fun FieldCard(title: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        content()
    }
}

/**
 * One cell of a value grid (e.g. the mL / g / % triplet): a bordered box with large
 * centered, color-coded text, and a small unit caption underneath. Editable when
 * [onValueChange] is non-null; the box turns pale yellow while focused, matching the
 * reference app's "active field" highlight.
 */
@Composable
fun RowScope.GridCell(
    value: String,
    unitLabel: String,
    onValueChange: ((String) -> Unit)?,
    color: Color,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
) {
    var focused by remember { mutableStateOf(false) }
    val readOnly = onValueChange == null
    val background = when {
        focused -> AppFieldColors.HighlightBackground
        readOnly -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    Column(
        modifier = modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        ValueBox(
            value = value,
            onValueChange = onValueChange,
            color = color,
            background = background,
            onFocusChange = { focused = it },
        )
        Text(
            text = unitLabel,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        trailing?.invoke()
    }
}

/** Label-left / value-right row used by the single-column calculator screens. */
@Composable
fun LabeledValueRow(
    label: String,
    value: String,
    onValueChange: ((String) -> Unit)?,
    color: Color,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
) {
    var focused by remember { mutableStateOf(false) }
    val readOnly = onValueChange == null
    val background = when {
        focused || highlighted -> AppFieldColors.HighlightBackground
        readOnly -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        ValueBox(
            value = value,
            onValueChange = onValueChange,
            color = color,
            background = background,
            onFocusChange = { focused = it },
            modifier = Modifier.width(130.dp),
        )
    }
}

@Composable
private fun ValueBox(
    value: String,
    onValueChange: ((String) -> Unit)?,
    color: Color,
    background: Color,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val readOnly = onValueChange == null
    val animatedBackground by animateColorAsState(background, label = "valueBoxBackground")
    val textStyle = TextStyle(
        color = color,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
    )
    val boxModifier = modifier
        .background(animatedBackground, RoundedCornerShape(4.dp))
        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
        .padding(horizontal = 8.dp, vertical = 10.dp)

    if (readOnly) {
        AnimatedContent(
            targetState = value,
            transitionSpec = {
                (slideInVertically(tween(200)) { it / 2 } + fadeIn(tween(200))) togetherWith
                    (slideOutVertically(tween(200)) { -it / 2 } + fadeOut(tween(150)))
            },
            modifier = boxModifier.fillMaxWidth(),
            label = "valueBoxContent",
        ) { animatedValue ->
            Text(text = animatedValue, style = textStyle, modifier = Modifier.fillMaxWidth())
        }
    } else {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = textStyle,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(color),
            modifier = boxModifier
                .fillMaxWidth()
                .onFocusChanged { onFocusChange(it.isFocused) },
        )
    }
}

/** Small "−"/"+" stepper pair, used next to editable percentage fields. */
@Composable
fun StepperButtons(onDecrement: () -> Unit, onIncrement: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        StepperButton(symbol = "−", onClick = onDecrement)
        StepperButton(symbol = "+", onClick = onIncrement)
    }
}

@Composable
private fun StepperButton(symbol: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource, pressedScale = 0.88f)

    Row(
        modifier = Modifier
            .size(32.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onClick, interactionSource = interactionSource, modifier = Modifier.size(32.dp)) {
            Text(symbol, style = MaterialTheme.typography.titleLarge, color = LocalContentColor.current)
        }
    }
}

/** A plain "label: value" inline pair used for compact result summaries (e.g. rectification cuts). */
@Composable
fun PlainResultLine(label: String, value: String, color: Color = AppFieldColors.Neutral) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = "$label: ", style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = color, fontWeight = FontWeight.SemiBold)
    }
}

/** Full-width outlined reset button matching the reference app's muted "СБРОС" button. */
@Composable
fun ResetButton(label: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource, pressedScale = 0.97f)

    OutlinedButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale },
    ) {
        Text(label)
    }
}

/** Full-width filled action button (e.g. "Start") with tactile press-scale feedback. */
@Composable
fun PrimaryActionButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, height: Dp = 56.dp) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource)

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .graphicsLayer { scaleX = scale; scaleY = scale },
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium)
    }
}

/** Full-width outlined action button (e.g. "Stop") with tactile press-scale feedback. */
@Composable
fun OutlinedActionButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, height: Dp = 56.dp) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource)

    OutlinedButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .graphicsLayer { scaleX = scale; scaleY = scale },
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium)
    }
}

/** Small italic hint used to flag approximate/simplified formulas or invalid-input guidance. */
@Composable
fun HintText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(fontStyle = FontStyle.Italic),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 4.dp),
    )
}
