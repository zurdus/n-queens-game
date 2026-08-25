package com.zurdus.nqueens.ui.preview

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zurdus.nqueens.ui.theme.NQueensTheme

@Preview(
    name = "Light",
    group = "Theme",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Dark",
    group = "Theme",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "Large",
    group = "Font scale",
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.3f,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Accessibility",
    group = "Font scale",
    widthDp = 360,
    heightDp = 800,
    fontScale = 2f,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Compact portrait",
    group = "Display size",
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Compact landscape",
    group = "Display size",
    widthDp = 800,
    heightDp = 360,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Medium tablet",
    group = "Display size",
    widthDp = 700,
    heightDp = 960,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Expanded tablet",
    group = "Display size",
    widthDp = 1000,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
internal annotation class NQueensPreviews

@Composable
internal fun NQueensPreview(
    content: @Composable () -> Unit,
) {
    NQueensTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            content = content,
        )
    }
}
