package com.zurdus.nqueens.feature.game.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.zurdus.nqueens.R
import com.zurdus.nqueens.feature.game.domain.model.BoardSquare
import com.zurdus.nqueens.ui.component.ChessBoard
import com.zurdus.nqueens.ui.preview.NQueensPreview
import com.zurdus.nqueens.ui.preview.NQueensPreviews
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun GameScreen(
    boardSize: Int,
    onNavigateBack: () -> Unit,
    viewModel: GameViewModel = koinViewModel(
        parameters = { parametersOf(boardSize) },
    ),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    GameScreen(
        state = state,
        onNavigateBack = onNavigateBack,
        onCellClick = viewModel::onCellClicked,
        onUndoClick = viewModel::onUndoClicked,
        onResetClick = viewModel::onResetClicked,
    )
}

@Composable
private fun GameScreen(
    state: GameUiState,
    onNavigateBack: () -> Unit,
    onCellClick: (row: Int, column: Int) -> Unit,
    onUndoClick: () -> Unit,
    onResetClick: () -> Unit,
) {
    val layout = currentWindowAdaptiveInfoV2()
        .windowSizeClass
        .toGameLayout()

    Scaffold(
        topBar = {
            GameTopBar(
                onNavigateBack = onNavigateBack,
            )
        },
        bottomBar = {
            GameBottomBar(
                hasQueens = state.queenSquares.isNotEmpty(),
                canUndo = state.canUndo,
                isSolved = state.isSolved,
                onUndoClick = onUndoClick,
                onResetClick = onResetClick,
            )
        },
    ) { contentPadding ->
        GameScreenContent(
            state = state,
            layout = layout,
            onCellClick = onCellClick,
            modifier = Modifier.padding(contentPadding),
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun GameTopBar(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.game_title),
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag(GAME_BACK_TEST_TAG),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.game_back),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

@Composable
private fun GameScreenContent(
    state: GameUiState,
    layout: GameLayout,
    onCellClick: (row: Int, column: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (layout) {
        GameLayout.VERTICAL -> GameVerticalLayout(
            state = state,
            onCellClick = onCellClick,
            modifier = modifier,
        )
        GameLayout.HORIZONTAL -> GameHorizontalLayout(
            state = state,
            onCellClick = onCellClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun GameVerticalLayout(
    state: GameUiState,
    onCellClick: (row: Int, column: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag(GAME_CONTENT_TEST_TAG),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GameStatusBanner(
            state = state,
            modifier = Modifier
                .widthIn(max = MAX_BOARD_WIDTH)
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        GameBoard(
            state = state,
            onCellClick = onCellClick,
            modifier = Modifier
                .widthIn(max = MAX_BOARD_WIDTH)
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        GameProgressCard(
            state = state,
            modifier = Modifier
                .widthIn(max = MAX_BOARD_WIDTH)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun GameHorizontalLayout(
    state: GameUiState,
    onCellClick: (row: Int, column: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag(GAME_CONTENT_TEST_TAG),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            val boardDimension = minOf(maxWidth, maxHeight, MAX_BOARD_WIDTH)
            GameBoard(
                state = state,
                onCellClick = onCellClick,
                modifier = Modifier.size(boardDimension),
            )
        }

        Box(
            modifier = Modifier
                .weight(0.72f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 380.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                GameStatusBanner(
                    state = state,
                    modifier = Modifier.fillMaxWidth(),
                )
                GameProgressCard(
                    state = state,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun GameProgressCard(
    state: GameUiState,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.game_your_progress),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(
                        R.string.game_progress,
                        state.queenSquares.size,
                        state.boardSize,
                    ),
                    modifier = Modifier.testTag(GAME_PROGRESS_TEST_TAG),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { state.queenSquares.size.toFloat() / state.boardSize },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = if (state.conflictingQueenSquares.isEmpty()) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.error
                },
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                drawStopIndicator = {},
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(
                    R.string.game_instruction,
                    state.boardSize,
                ),
                modifier = Modifier.testTag(GAME_INSTRUCTION_TEST_TAG),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun GameBoard(
    state: GameUiState,
    onCellClick: (row: Int, column: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val queenSquares = state.queenSquares
    val conflictingQueenSquares = state.conflictingQueenSquares

    ChessBoard(
        boardSize = state.boardSize,
        contentDescription = stringResource(
            R.string.game_board_content_description,
            state.boardSize,
            state.boardSize,
        ),
        modifier = modifier.testTag(GAME_CHESS_BOARD_TEST_TAG),
        isQueenAt = { row, column -> BoardSquare(row, column) in queenSquares },
        isConflictingAt = { row, column ->
            BoardSquare(row, column) in conflictingQueenSquares
        },
        onCellClick = onCellClick,
    )
}

@Composable
private fun GameStatusBanner(
    state: GameUiState,
    modifier: Modifier = Modifier,
) {
    val status = gameStatus(state)

    Surface(
        modifier = modifier
            .semantics { liveRegion = LiveRegionMode.Polite }
            .testTag(GAME_STATUS_TEST_TAG),
        color = status.containerColor,
        contentColor = status.contentColor,
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                color = status.contentColor.copy(alpha = 0.12f),
                contentColor = status.contentColor,
                shape = CircleShape,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = status.symbol,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Column {
                Text(
                    text = stringResource(status.title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(status.message, status.messageArgument),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun gameStatus(state: GameUiState): GameStatus = when {
    state.isSolved -> GameStatus(
        symbol = "✓",
        title = R.string.game_status_solved_title,
        message = R.string.game_status_solved_message,
        messageArgument = state.boardSize,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    )
    state.conflictingQueenSquares.isNotEmpty() -> GameStatus(
        symbol = "!",
        title = R.string.game_status_conflict_title,
        message = R.string.game_status_conflict_message,
        messageArgument = state.conflictingQueenSquares.size,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
    )
    state.queenSquares.isEmpty() -> GameStatus(
        symbol = "♛",
        title = R.string.game_status_ready_title,
        message = R.string.game_status_ready_message,
        messageArgument = state.boardSize,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
    else -> GameStatus(
        symbol = "✓",
        title = R.string.game_status_safe_title,
        message = R.string.game_status_safe_message,
        messageArgument = state.queensLeft,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    )
}

@Composable
private fun GameBottomBar(
    hasQueens: Boolean,
    canUndo: Boolean,
    isSolved: Boolean,
    onUndoClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(
                    WindowInsets.navigationBars
                )
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier
                    .widthIn(max = MAX_CONTROLS_WIDTH)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onUndoClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag(GAME_UNDO_TEST_TAG),
                    enabled = canUndo,
                ) {
                    Text(text = stringResource(R.string.game_undo))
                }
                FilledTonalButton(
                    onClick = onResetClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag(GAME_RESET_TEST_TAG),
                    enabled = hasQueens,
                ) {
                    Text(
                        text = stringResource(
                            if (isSolved) R.string.game_play_again else R.string.game_reset,
                        ),
                    )
                }
            }
        }
    }
}

@NQueensPreviews
@Composable
private fun GameScreenPreview() {
    NQueensPreview {
        GameScreen(
            state = GameUiState(
                boardSize = 8,
                queenSquares = setOf(
                    BoardSquare(0, 0),
                    BoardSquare(2, 3),
                    BoardSquare(5, 6),
                ),
            ),
            onNavigateBack = {},
            onCellClick = { _, _ -> },
            onUndoClick = {},
            onResetClick = {},
        )
    }
}

private fun WindowSizeClass.toGameLayout(): GameLayout =
    if (isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
        GameLayout.HORIZONTAL
    } else {
        GameLayout.VERTICAL
    }

private data class GameStatus(
    val symbol: String,
    val title: Int,
    val message: Int,
    val messageArgument: Int,
    val containerColor: Color,
    val contentColor: Color,
)

private enum class GameLayout {
    VERTICAL,
    HORIZONTAL,
}

internal const val GAME_BACK_TEST_TAG = "game_back"
internal const val GAME_CHESS_BOARD_TEST_TAG = "game_chess_board"
internal const val GAME_CONTENT_TEST_TAG = "game_content"
internal const val GAME_INSTRUCTION_TEST_TAG = "game_instruction"
internal const val GAME_PROGRESS_TEST_TAG = "game_progress"
internal const val GAME_RESET_TEST_TAG = "game_reset"
internal const val GAME_STATUS_TEST_TAG = "game_status"
internal const val GAME_UNDO_TEST_TAG = "game_undo"

private val MAX_BOARD_WIDTH = 560.dp
private val MAX_CONTROLS_WIDTH = 560.dp
