package com.zurdus.nqueens.feature.boardsize.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.zurdus.nqueens.R
import com.zurdus.nqueens.feature.boardsize.presentation.component.BoardPreview
import com.zurdus.nqueens.ui.preview.NQueensPreview
import com.zurdus.nqueens.ui.preview.NQueensPreviews
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
internal fun BoardSizeScreen(
    viewModel: BoardSizeViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BoardSizeScreen(
        state = state,
        onBoardSizeChanged = viewModel::onBoardSizeChanged,
    )
}

@Composable
private fun BoardSizeScreen(
    state: BoardSizeUiState,
    onBoardSizeChanged: (Int) -> Unit,
) {
    val layout = currentWindowAdaptiveInfoV2()
        .windowSizeClass
        .toBoardSizeLayout()

    Scaffold { contentPadding ->
        BoardSizeScreenContent(
            state = state,
            onBoardSizeChanged = onBoardSizeChanged,
            layout = layout,
            modifier = Modifier.padding(contentPadding),
        )
    }
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
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BoardSizeControls(
                state = state,
                onBoardSizeChanged = onBoardSizeChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = MAX_CONTROLS_WIDTH),
            )

            Spacer(modifier = Modifier.height(32.dp))

            BoardPreview(
                boardSize = state.selectedSize,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = boardMaxWidth),
            )

            Spacer(modifier = Modifier.height(12.dp))

            BoardSizeSelectedValue(selectedSize = state.selectedSize)
        }
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
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            BoardSizeControls(
                state = state,
                onBoardSizeChanged = onBoardSizeChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = MAX_CONTROLS_WIDTH)
                    .verticalScroll(rememberScrollState()),
            )
        }

        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            val selectedValueHeight = with(LocalDensity.current) {
                MaterialTheme.typography.displaySmall.lineHeight.toDp()
            }
            val boardDimension = minOf(
                maxWidth,
                (maxHeight - selectedValueHeight - 12.dp).coerceAtLeast(0.dp),
                MAX_BOARD_WIDTH,
            )

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BoardPreview(
                    boardSize = state.selectedSize,
                    modifier = Modifier.size(boardDimension),
                )

                Spacer(modifier = Modifier.height(12.dp))

                BoardSizeSelectedValue(selectedSize = state.selectedSize)
            }
        }
    }
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
        style = MaterialTheme.typography.displaySmall,
    )
}

@Composable
private fun BoardSizeControls(
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

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.board_size_title),
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(24.dp))

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

@NQueensPreviews
@Composable
private fun BoardSizeScreenPreview() {
    NQueensPreview {
        BoardSizeScreen(
            state = BoardSizeUiState(),
            onBoardSizeChanged = {},
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
internal const val BOARD_SIZE_SLIDER_TEST_TAG = "board_size_slider"

private val MAX_CONTROLS_WIDTH = 360.dp
private val MAX_BOARD_WIDTH = 560.dp
