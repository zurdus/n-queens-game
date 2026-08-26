package com.zurdus.nqueens.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.zurdus.nqueens.ui.theme.boardColors

@Composable
internal fun ChessBoard(
    boardSize: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    require(boardSize > 0) { "Board size must be positive." }

    val boardColors = MaterialTheme.boardColors

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            .semantics(mergeDescendants = true) {
                this.contentDescription = contentDescription
            },
    ) {
        repeat(boardSize) { row ->
            ChessBoardRow(
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
private fun ChessBoardRow(
    row: Int,
    boardSize: Int,
    lightSquareColor: Color,
    darkSquareColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        repeat(boardSize) { column ->
            ChessBoardSquare(
                color = if (isLightSquare(row, column)) {
                    lightSquareColor
                } else {
                    darkSquareColor
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .testTag(chessBoardCellTestTag(row, column)),
            )
        }
    }
}

@Composable
private fun ChessBoardSquare(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.background(color))
}

internal fun chessBoardCellTestTag(row: Int, column: Int): String =
    "chess_board_cell_${row}_$column"

internal fun isLightSquare(row: Int, column: Int): Boolean =
    (row + column) % 2 == 0
