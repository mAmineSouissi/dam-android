package tn.esprit.dam_android.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tn.esprit.dam_android.ui.theme.DarkColors
import tn.esprit.dam_android.ui.theme.Spacing

enum class BadgeSeverity {
    SUCCESS,
    WARNING,
    ERROR,
    INFO
}

@Composable
fun SGBadge(
    text: String,
    severity: BadgeSeverity,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (severity) {
        BadgeSeverity.SUCCESS -> DarkColors.Success.copy(alpha = 0.2f)
        BadgeSeverity.WARNING -> DarkColors.Warning.copy(alpha = 0.2f)
        BadgeSeverity.ERROR -> DarkColors.Error.copy(alpha = 0.2f)
        BadgeSeverity.INFO -> DarkColors.Info.copy(alpha = 0.2f)
    }

    val textColor = when (severity) {
        BadgeSeverity.SUCCESS -> DarkColors.Success
        BadgeSeverity.WARNING -> DarkColors.Warning
        BadgeSeverity.ERROR -> DarkColors.Error
        BadgeSeverity.INFO -> DarkColors.Info
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                horizontal = Spacing.sm,
                vertical = Spacing.xs
            )
        )
    }
}