package com.zurdus.nqueens.feature.boardsize.presentation

import com.zurdus.nqueens.feature.boardsize.domain.BoardSizeRules
import org.junit.Assert.assertEquals
import org.junit.Test

class BoardSizeViewModelTest {

    @Test
    fun `initial state uses board-size rules`() {
        val viewModel = BoardSizeViewModel()

        assertEquals(BoardSizeUiState(), viewModel.uiState.value)
    }

    @Test
    fun `board-size change accepts every supported size`() {
        val viewModel = BoardSizeViewModel()

        (BoardSizeRules.MIN..BoardSizeRules.MAX).forEach { size ->
            viewModel.onBoardSizeChanged(size)

            assertEquals(size, viewModel.uiState.value.selectedSize)
        }
    }

    @Test
    fun `board-size change clamps unsupported sizes`() {
        val viewModel = BoardSizeViewModel()

        viewModel.onBoardSizeChanged(Int.MIN_VALUE)
        assertEquals(BoardSizeRules.MIN, viewModel.uiState.value.selectedSize)

        viewModel.onBoardSizeChanged(Int.MAX_VALUE)
        assertEquals(BoardSizeRules.MAX, viewModel.uiState.value.selectedSize)
    }
}
