package com.zurdus.nqueens.feature.boardsize.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.zurdus.nqueens.R
import com.zurdus.nqueens.feature.boardsize.domain.BoardSizeRules
import com.zurdus.nqueens.ui.preview.NQueensPreview
import com.zurdus.nqueens.ui.preview.NQueensPreviews
import com.zurdus.nqueens.ui.theme.boardColors

@Composable
internal fun BoardPreview(
    boardSize: Int,
    modifier: Modifier = Modifier,
) {
    require(boardSize in BoardSizeRules.MIN..BoardSizeRules.MAX) {
        "Board size must be between ${BoardSizeRules.MIN} and ${BoardSizeRules.MAX}."
    }

    val boardColors = MaterialTheme.boardColors
    val boardDescription = stringResource(
        R.string.board_preview_content_description,
        boardSize,
        boardSize,
    )

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            .semantics(mergeDescendants = true) {
                contentDescription = boardDescription
            }
            .testTag(BOARD_PREVIEW_TEST_TAG),
    ) {
        repeat(boardSize) { row ->
            BoardRow(
                row = row,
                boardSize = boardSize,
                lightSquareColor = boardColors.lightSquare,
                darkSquareColor = boardColors.darkSquare,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun BoardRow(
    row: Int,
    boardSize: Int,
    lightSquareColor: Color,
    darkSquareColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        repeat(boardSize) { column ->
            BoardSquare(
                color = if (isLightSquare(row, column)) {
                    lightSquareColor
                } else {
                    darkSquareColor
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .testTag(boardCellTestTag(row, column)),
            )
        }
    }
}

@Composable
private fun BoardSquare(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.background(color),
    )
}

internal class BoardSizePreviewParameterProvider : PreviewParameterProvider<Int> {
    override val values = sequenceOf(
        BoardSizeRules.MIN,
        BoardSizeRules.DEFAULT,
        BoardSizeRules.MAX,
    )
}

@NQueensPreviews
@Composable
private fun BoardPreviewPreview(
    @PreviewParameter(BoardSizePreviewParameterProvider::class) boardSize: Int,
) {
    NQueensPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            BoardPreview(
                boardSize = boardSize,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

internal fun boardCellTestTag(row: Int, column: Int): String =
    "board_cell_${row}_$column"

internal fun isLightSquare(row: Int, column: Int): Boolean =
    (row + column) % 2 == 0

internal const val BOARD_PREVIEW_TEST_TAG = "board_preview"
