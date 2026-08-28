package com.zurdus.nqueens.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.zurdus.nqueens.R
import com.zurdus.nqueens.ui.motion.NQueensMotion
import com.zurdus.nqueens.ui.theme.boardColors

@Composable
internal fun ChessBoard(
    boardSize: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    isQueenAt: (row: Int, column: Int) -> Boolean = { _, _ -> false },
    isConflictingAt: (row: Int, column: Int) -> Boolean = { _, _ -> false },
    onCellClick: ((row: Int, column: Int) -> Unit)? = null,
) {
    require(boardSize > 0) { "Board size must be positive." }

    val boardColors = MaterialTheme.boardColors

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline,
            )
            .semantics(mergeDescendants = onCellClick == null) {
                this.contentDescription = contentDescription
            },
    ) {
        repeat(boardSize) { row ->
            ChessBoardRow(
                row = row,
                boardSize = boardSize,
                lightSquareColor = boardColors.lightSquare,
                darkSquareColor = boardColors.darkSquare,
                isQueenAt = isQueenAt,
                isConflictingAt = isConflictingAt,
                onCellClick = onCellClick,
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
    isQueenAt: (row: Int, column: Int) -> Boolean,
    isConflictingAt: (row: Int, column: Int) -> Boolean,
    onCellClick: ((row: Int, column: Int) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        repeat(boardSize) { column ->
            val hasQueen = isQueenAt(row, column)
            val hasConflict = isConflictingAt(row, column)
            ChessBoardSquare(
                row = row,
                column = column,
                color = if (isLightSquare(row, column)) {
                    lightSquareColor
                } else {
                    darkSquareColor
                },
                hasQueen = hasQueen,
                hasConflict = hasConflict,
                onClick = onCellClick?.let { click -> { click(row, column) } },
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
    row: Int,
    column: Int,
    color: Color,
    hasQueen: Boolean,
    hasConflict: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val cellDescription = when {
        hasConflict -> stringResource(
            R.string.game_cell_conflict_content_description,
            row + 1,
            column + 1,
        )
        hasQueen -> stringResource(
            R.string.game_cell_queen_content_description,
            row + 1,
            column + 1,
        )
        else -> stringResource(
            R.string.game_cell_empty_content_description,
            row + 1,
            column + 1,
        )
    }
    val interactiveModifier = if (onClick == null) {
        Modifier
    } else {
        Modifier
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = cellDescription
                role = Role.Button
            }
    }

    BoxWithConstraints(
        modifier = modifier
            .background(
                color = if (hasConflict) {
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.86f)
                } else {
                    color
                },
            )
            .then(interactiveModifier),
        contentAlignment = Alignment.Center,
    ) {
        val queenFontSize = with(LocalDensity.current) {
            (maxWidth * QUEEN_FONT_SIZE_FRACTION).toSp()
        }
        val pieceBackground = if (hasConflict) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        }
        val pieceColor = if (hasConflict) {
            MaterialTheme.colorScheme.onError
        } else {
            MaterialTheme.colorScheme.onSecondaryContainer
        }

        AnimatedVisibility(
            visible = hasQueen,
            enter = NQueensMotion.queenEnter,
            exit = NQueensMotion.queenExit,
        ) {
            ChessQueen(
                fontSize = queenFontSize,
                backgroundColor = pieceBackground,
                contentColor = pieceColor,
            )
        }
    }
}

@Composable
private fun ChessQueen(
    fontSize: TextUnit,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize(0.74f)
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = QUEEN_GLYPH,
            color = contentColor,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            lineHeight = fontSize,
            textAlign = TextAlign.Center,
        )
    }
}

internal fun chessBoardCellTestTag(row: Int, column: Int): String =
    "chess_board_cell_${row}_$column"

internal fun isLightSquare(row: Int, column: Int): Boolean =
    (row + column) % 2 == 0

private const val QUEEN_FONT_SIZE_FRACTION = 0.58f
private const val QUEEN_GLYPH = "♛"
