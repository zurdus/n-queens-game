package com.zurdus.nqueens.feature.boardsize.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.zurdus.nqueens.R
import com.zurdus.nqueens.feature.boardsize.domain.BoardSizeRules
import com.zurdus.nqueens.ui.component.ChessBoard
import com.zurdus.nqueens.ui.preview.NQueensPreview
import com.zurdus.nqueens.ui.preview.NQueensPreviews

@Composable
internal fun ChessBoardPreview(
    boardSize: Int,
    modifier: Modifier = Modifier,
) {
    require(boardSize in BoardSizeRules.MIN..BoardSizeRules.MAX) {
        "Board size must be between ${BoardSizeRules.MIN} and ${BoardSizeRules.MAX}."
    }

    ChessBoard(
        boardSize = boardSize,
        contentDescription = stringResource(
            R.string.board_preview_content_description,
            boardSize,
            boardSize,
        ),
        modifier = modifier.testTag(CHESS_BOARD_PREVIEW_TEST_TAG),
    )
}

@NQueensPreviews
@Composable
private fun ChessBoardPreviewPreview(
    @PreviewParameter(ChessBoardPreviewParameterProvider::class) boardSize: Int,
) {
    NQueensPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            ChessBoardPreview(
                boardSize = boardSize,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

internal class ChessBoardPreviewParameterProvider : PreviewParameterProvider<Int> {
    override val values = sequenceOf(
        BoardSizeRules.MIN,
        BoardSizeRules.DEFAULT,
        BoardSizeRules.MAX,
    )
}

internal const val CHESS_BOARD_PREVIEW_TEST_TAG = "chess_board_preview"
