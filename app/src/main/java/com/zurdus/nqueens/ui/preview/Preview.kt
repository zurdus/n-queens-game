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
