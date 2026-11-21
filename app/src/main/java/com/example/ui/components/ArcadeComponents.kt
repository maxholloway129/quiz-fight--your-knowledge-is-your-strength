package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ArcadeAmber
import com.example.ui.theme.ArcadeCream
import com.example.ui.theme.ArcadeCrimson
import com.example.ui.theme.ArcadeEmerald
import com.example.ui.theme.ArcadeGold
import com.example.ui.theme.ArcadeGoldDark
import com.example.ui.theme.ArcadeInputBg
import com.example.ui.theme.ArcadeMutedText
import com.example.ui.theme.ArcadePanel
import com.example.ui.theme.ArcadePanelBorderDark
import com.example.ui.theme.ArcadePanelBorderLight
import com.example.ui.theme.ArcadeVoid

@Composable
fun ArcadePanel(
    modifier: Modifier = Modifier,
    borderColor: Color = ArcadePanelBorderLight,
    backgroundColor: Color = ArcadePanel,
    contentPadding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(backgroundColor)
            .border(3.dp, ArcadePanelBorderDark)
            .padding(2.dp)
            .border(2.dp, borderColor)
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun ArcadeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = ArcadeGold,
    shadowColor: Color = ArcadeGoldDark,
    contentColor: Color = ArcadeVoid,
    minHeight: Dp = 52.dp
) {
    val bg = if (enabled) containerColor else Color(0xFF3E355E)
    val rim = if (enabled) shadowColor else Color(0xFF282140)
    val textCol = if (enabled) contentColor else ArcadeMutedText.copy(alpha = 0.6f)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .heightIn(min = minHeight)
            .background(ArcadePanelBorderDark)
            .padding(bottom = 4.dp, end = 2.dp)
            .background(rim)
            .padding(bottom = 4.dp)
            .background(bg)
            .border(2.dp, if (enabled) Color.White.copy(alpha = 0.4f) else Color.Transparent)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = Color.White),
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
            color = textCol,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SoundToggleButton(
    isSoundEnabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val accent = if (isSoundEnabled) ArcadeEmerald else ArcadeCrimson
    val label = if (isSoundEnabled) stringResource(R.string.sound_on) else stringResource(R.string.sound_off)
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RectangleShape)
            .background(ArcadeVoid.copy(alpha = 0.88f))
            .border(2.dp, accent)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = accent),
                onClick = onToggle
            )
            .padding(horizontal = if (compact) 8.dp else 12.dp, vertical = if (compact) 6.dp else 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSoundEnabled) {
                Icons.AutoMirrored.Filled.VolumeUp
            } else {
                Icons.AutoMirrored.Filled.VolumeOff
            },
            contentDescription = label,
            tint = accent,
            modifier = Modifier.size(if (compact) 16.dp else 20.dp)
        )
        if (!compact) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = accent
            )
        }
    }
}

@Composable
fun ArcadeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else 4,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: (() -> Unit)? = null,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = singleLine,
        maxLines = maxLines,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = ArcadeMutedText.copy(alpha = 0.65f)
            )
        },
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = ArcadeCream),
        shape = RoundedCornerShape(2.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = ArcadeInputBg,
            unfocusedContainerColor = ArcadeInputBg,
            disabledContainerColor = ArcadeInputBg.copy(alpha = 0.65f),
            focusedBorderColor = if (isError) ArcadeCrimson else ArcadeGold,
            unfocusedBorderColor = if (isError) ArcadeCrimson else ArcadePanelBorderLight,
            disabledBorderColor = ArcadePanelBorderDark,
            focusedTextColor = ArcadeCream,
            unfocusedTextColor = ArcadeCream,
            disabledTextColor = ArcadeMutedText,
            cursorColor = ArcadeGold
        ),
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = { onImeAction?.invoke() },
            onNext = { onImeAction?.invoke() },
            onSend = { onImeAction?.invoke() }
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun PixelDivider(
    modifier: Modifier = Modifier,
    color: Color = ArcadeAmber
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp)
            .background(color)
    )
}
