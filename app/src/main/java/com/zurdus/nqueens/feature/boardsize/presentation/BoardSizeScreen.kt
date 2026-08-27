package com.zurdus.nqueens.feature.boardsize.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.zurdus.nqueens.R
import com.zurdus.nqueens.ui.component.ChessBoard
import com.zurdus.nqueens.ui.preview.NQueensPreview
import com.zurdus.nqueens.ui.preview.NQueensPreviews
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
internal fun BoardSizeScreen(
    onStartGame: (Int) -> Unit,
    viewModel: BoardSizeViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BoardSizeScreen(
        state = state,
        onBoardSizeChanged = viewModel::onBoardSizeChanged,
        onStartGame = onStartGame,
    )
}

@Composable
private fun BoardSizeScreen(
    state: BoardSizeUiState,
    onBoardSizeChanged: (Int) -> Unit,
    onStartGame: (Int) -> Unit,
) {
    val layout = currentWindowAdaptiveInfoV2()
        .windowSizeClass
        .toBoardSizeLayout()

    Scaffold(
        topBar = {
            BoardSizeTopBar()
        },
        bottomBar = {
            BoardSizeBottomBar(
                onStartGame = {
                    onStartGame(state.selectedSize)
                },
            )
        },
    ) { contentPadding ->
        BoardSizeScreenContent(
            state = state,
            onBoardSizeChanged = onBoardSizeChanged,
            layout = layout,
            modifier = Modifier.padding(contentPadding),
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BoardSizeTopBar(
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

@Composable
private fun BoardSizeScreenContent(
    state: BoardSizeUiState,
    onBoardSizeChanged: (Int) -> Unit,
    layout: BoardSizeLayout,
    modifier: Modifier = Modifier,
) {
    when (layout) {
        BoardSizeLayout.VERTICAL_COMPACT -> BoardSizeVerticalLayout(
            state = state,
            onBoardSizeChanged = onBoardSizeChanged,
            boardMaxWidth = MAX_BOARD_WIDTH,
            modifier = modifier,
        )

        BoardSizeLayout.VERTICAL_MEDIUM -> BoardSizeVerticalLayout(
            state = state,
            onBoardSizeChanged = onBoardSizeChanged,
            boardMaxWidth = 480.dp,
            modifier = modifier,
        )

        BoardSizeLayout.HORIZONTAL -> BoardSizeHorizontalLayout(
            state = state,
            onBoardSizeChanged = onBoardSizeChanged,
            modifier = modifier,
        )
    }
}

@Composable
private fun BoardSizeVerticalLayout(
    state: BoardSizeUiState,
    onBoardSizeChanged: (Int) -> Unit,
    boardMaxWidth: Dp,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BoardSizeSelectorCard(
            state = state,
            onBoardSizeChanged = onBoardSizeChanged,
            modifier = Modifier
                .widthIn(max = boardMaxWidth)
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        BoardSizePreview(
            state = state,
            modifier = Modifier
                .widthIn(max = boardMaxWidth)
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        BoardSizeSelectedValue(selectedSize = state.selectedSize)
    }
}

@Composable
private fun BoardSizeHorizontalLayout(
    state: BoardSizeUiState,
    onBoardSizeChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            val selectedValueHeight = with(LocalDensity.current) {
                MaterialTheme.typography.headlineSmall.lineHeight.toDp()
            }
            val availableBoardHeight =
                (maxHeight - selectedValueHeight - 12.dp).coerceAtLeast(0.dp)
            val boardDimension = minOf(
                maxWidth,
                availableBoardHeight,
                MAX_BOARD_WIDTH,
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BoardSizePreview(
                    state = state,
                    modifier = Modifier.size(boardDimension),
                )

                Spacer(modifier = Modifier.height(12.dp))

                BoardSizeSelectedValue(selectedSize = state.selectedSize)
            }
        }

        Box(
            modifier = Modifier
                .weight(0.72f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = MAX_PANEL_WIDTH)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                BoardSizeSelectorCard(
                    state = state,
                    onBoardSizeChanged = onBoardSizeChanged,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun BoardSizePreview(
    state: BoardSizeUiState,
    modifier: Modifier = Modifier,
) {
    ChessBoard(
        boardSize = state.selectedSize,
        contentDescription = stringResource(
            R.string.board_preview_content_description,
            state.selectedSize,
            state.selectedSize,
        ),
        modifier = modifier.testTag(BOARD_SIZE_CHESS_BOARD_TEST_TAG),
    )
}

@Composable
private fun BoardSizeSelectedValue(
    selectedSize: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(
            R.string.board_size_dimension,
            selectedSize,
            selectedSize,
        ),
        modifier = modifier.testTag(BOARD_SIZE_SELECTED_VALUE_TEST_TAG),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun BoardSizeSelectorCard(
    state: BoardSizeUiState,
    onBoardSizeChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val minimumSizeLabel = stringResource(
        R.string.board_size_dimension,
        state.minimumSize,
        state.minimumSize,
    )
    val maximumSizeLabel = stringResource(
        R.string.board_size_dimension,
        state.maximumSize,
        state.maximumSize,
    )
    val sliderDescription = stringResource(R.string.board_size_slider_content_description)

    Surface(
        modifier = modifier.testTag(BOARD_SIZE_CONTROLS_TEST_TAG),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = CircleShape,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "♛",
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.board_size_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Slider(
                value = state.selectedSize.toFloat(),
                onValueChange = { value -> onBoardSizeChanged(value.roundToInt()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = sliderDescription }
                    .testTag(BOARD_SIZE_SLIDER_TEST_TAG),
                valueRange = state.minimumSize.toFloat()..state.maximumSize.toFloat(),
                steps = state.maximumSize - state.minimumSize - 1,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = minimumSizeLabel,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = maximumSizeLabel,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun BoardSizeBottomBar(
    onStartGame: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Button(
                onClick = onStartGame,
                modifier = Modifier
                    .widthIn(max = MAX_ACTION_WIDTH)
                    .fillMaxWidth()
                    .testTag(BOARD_SIZE_START_GAME_TEST_TAG),
            ) {
                Text(text = stringResource(R.string.board_size_start_game))
            }
        }
    }
}

@NQueensPreviews
@Composable
private fun BoardSizeScreenPreview() {
    NQueensPreview {
        BoardSizeScreen(
            state = BoardSizeUiState(),
            onBoardSizeChanged = {},
            onStartGame = {},
        )
    }
}

internal fun WindowSizeClass.toBoardSizeLayout(): BoardSizeLayout = when {
    isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> BoardSizeLayout.HORIZONTAL
    isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) &&
            isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND) -> {
        BoardSizeLayout.VERTICAL_MEDIUM
    }

    isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> BoardSizeLayout.HORIZONTAL
    else -> BoardSizeLayout.VERTICAL_COMPACT
}

internal enum class BoardSizeLayout {
    VERTICAL_COMPACT,
    VERTICAL_MEDIUM,
    HORIZONTAL,
}

internal const val BOARD_SIZE_SELECTED_VALUE_TEST_TAG = "board_size_selected_value"
internal const val BOARD_SIZE_CHESS_BOARD_TEST_TAG = "board_size_chess_board"
internal const val BOARD_SIZE_CONTROLS_TEST_TAG = "board_size_controls"
internal const val BOARD_SIZE_SLIDER_TEST_TAG = "board_size_slider"
internal const val BOARD_SIZE_START_GAME_TEST_TAG = "board_size_start_game"

private val MAX_ACTION_WIDTH = 560.dp
private val MAX_BOARD_WIDTH = 560.dp
private val MAX_PANEL_WIDTH = 380.dp
