package tn.esprit.dam_android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tn.esprit.dam_android.ui.theme.AppShapes
import tn.esprit.dam_android.ui.theme.Spacing

enum class ButtonVariant {
    PRIMARY,
    SECONDARY,
    GHOST
}

@Composable
fun SGButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    enabled: Boolean = true,
    icon: (@Composable () -> Unit)? = null
) {
    when (variant) {
        ButtonVariant.PRIMARY -> {
            Button(
                onClick = onClick,
                modifier = modifier
                    .height(48.dp),
                enabled = enabled,
                shape = AppShapes.button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                ),
                contentPadding = PaddingValues(
                    horizontal = Spacing.lg,
                    vertical = Spacing.md
                )
            ) {
                if (icon != null) {
                    icon()
                    Spacer(modifier = Modifier.width(Spacing.sm))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        ButtonVariant.SECONDARY -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier
                    .height(48.dp),
                enabled = enabled,
                shape = AppShapes.button,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                contentPadding = PaddingValues(
                    horizontal = Spacing.lg,
                    vertical = Spacing.md
                )
            ) {
                if (icon != null) {
                    icon()
                    Spacer(modifier = Modifier.width(Spacing.sm))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        ButtonVariant.GHOST -> {
            TextButton(
                onClick = onClick,
                modifier = modifier
                    .height(48.dp),
                enabled = enabled,
                shape = AppShapes.button,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
                contentPadding = PaddingValues(
                    horizontal = Spacing.lg,
                    vertical = Spacing.md
                )
            ) {
                if (icon != null) {
                    icon()
                    Spacer(modifier = Modifier.width(Spacing.sm))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun SGIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(40.dp),
        enabled = enabled
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = AppShapes.button,
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
        }
    }
}
