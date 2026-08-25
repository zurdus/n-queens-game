package com.zurdus.nqueens.feature.boardsize.presentation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zurdus.nqueens.feature.boardsize.presentation.component.BoardPreview
import com.zurdus.nqueens.ui.preview.NQueensPreview
import com.zurdus.nqueens.ui.preview.NQueensPreviews
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun BoardSizeScreen(
    viewModel: BoardSizeViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BoardSizeScreen(state = state)
}

@Composable
private fun BoardSizeScreen(
    state: BoardSizeUiState,
) {
    Scaffold { contentPadding ->
        BoardSizeScreenContent(
            state = state,
            modifier = Modifier.padding(contentPadding),
        )
    }
}

@Composable
private fun BoardSizeScreenContent(
    state: BoardSizeUiState,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        val boardDimension = minOf(maxWidth, maxHeight)

        BoardPreview(
            boardSize = state.selectedSize,
            modifier = Modifier.size(boardDimension),
        )
    }
}

@NQueensPreviews
@Composable
private fun BoardSizeScreenPreview() {
    NQueensPreview {
        BoardSizeScreen(state = BoardSizeUiState())
    }
}
