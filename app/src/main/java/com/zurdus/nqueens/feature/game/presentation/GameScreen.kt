package com.zurdus.nqueens.feature.game.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zurdus.nqueens.R
import com.zurdus.nqueens.ui.component.ChessBoard
import com.zurdus.nqueens.ui.preview.NQueensPreview
import com.zurdus.nqueens.ui.preview.NQueensPreviews
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun GameScreen(
    boardSize: Int,
    viewModel: GameViewModel = koinViewModel(
        parameters = { parametersOf(boardSize) },
    ),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    GameScreen(state = state)
}

@Composable
private fun GameScreen(
    state: GameUiState,
) {
    Scaffold { contentPadding ->
        GameScreenContent(
            state = state,
            modifier = Modifier.padding(contentPadding),
        )
    }
}

@Composable
private fun GameScreenContent(
    state: GameUiState,
    modifier: Modifier = Modifier,
) {
    Layout(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag(GAME_CONTENT_TEST_TAG),
        content = {
            Text(
                text = stringResource(
                    R.string.game_instruction,
                    state.boardSize,
                ),
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .testTag(GAME_INSTRUCTION_TEST_TAG),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )

            ChessBoard(
                boardSize = state.boardSize,
                contentDescription = stringResource(
                    R.string.game_board_content_description,
                    state.boardSize,
                    state.boardSize,
                ),
                modifier = Modifier.testTag(GAME_CHESS_BOARD_TEST_TAG),
            )
        },
    ) { measurables, constraints ->
        val instruction = measurables[0].measure(
            constraints.copy(minWidth = 0, minHeight = 0),
        )
        val instructionSpacing = 16.dp.roundToPx()
        val boardDimension = minOf(
            constraints.maxWidth,
            constraints.maxHeight - 2 * (instruction.height + instructionSpacing),
            560.dp.roundToPx(),
        ).coerceAtLeast(0)
        val board = measurables[1].measure(
            Constraints.fixed(boardDimension, boardDimension),
        )
        val boardX = (constraints.maxWidth - board.width) / 2
        val boardY = (constraints.maxHeight - board.height) / 2
        val instructionX = (constraints.maxWidth - instruction.width) / 2
        val instructionY = boardY - instructionSpacing - instruction.height

        layout(constraints.maxWidth, constraints.maxHeight) {
            instruction.placeRelative(instructionX, instructionY)
            board.placeRelative(boardX, boardY)
        }
    }
}

@NQueensPreviews
@Composable
private fun GameScreenPreview() {
    NQueensPreview {
        GameScreen(
            state = GameUiState(boardSize = 8),
        )
    }
}

internal const val GAME_CHESS_BOARD_TEST_TAG = "game_chess_board"
internal const val GAME_CONTENT_TEST_TAG = "game_content"
internal const val GAME_INSTRUCTION_TEST_TAG = "game_instruction"
