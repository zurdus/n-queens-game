package com.zurdus.nqueens.feature.game.presentation

import com.zurdus.nqueens.feature.game.domain.model.BoardPosition
import com.zurdus.nqueens.feature.game.domain.usecase.ChangeQueenPlacement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameViewModelTest {

    @Test
    fun `initial state uses provided board size`() {
        val viewModel = createViewModel(boardSize = 10)

        assertEquals(10, viewModel.uiState.value.boardSize)
        assertEquals(10, viewModel.uiState.value.queensLeft)
        assertTrue(viewModel.uiState.value.queens.isEmpty())
    }

    @Test
    fun `clicking a cell toggles its queen`() {
        val viewModel = createViewModel(boardSize = 4)
        val position = BoardPosition(row = 1, column = 2)

        viewModel.onCellClicked(row = position.row, column = position.column)
        assertEquals(setOf(position), viewModel.uiState.value.queens)

        viewModel.onCellClicked(row = position.row, column = position.column)
        assertTrue(viewModel.uiState.value.queens.isEmpty())
    }

    @Test
    fun `conflicting placements update render state`() {
        val viewModel = createViewModel(boardSize = 4)
        val firstQueen = BoardPosition(row = 0, column = 0)
        val secondQueen = BoardPosition(row = 0, column = 2)

        viewModel.onCellClicked(firstQueen.row, firstQueen.column)
        viewModel.onCellClicked(secondQueen.row, secondQueen.column)

        assertEquals(setOf(firstQueen, secondQueen), viewModel.uiState.value.conflictingQueens)
        assertFalse(viewModel.uiState.value.isSolved)
    }

    @Test
    fun `board never accepts more queens than its size`() {
        val viewModel = createViewModel(boardSize = 4)
        repeat(4) { column -> viewModel.onCellClicked(row = 0, column = column) }

        viewModel.onCellClicked(row = 1, column = 0)

        assertEquals(4, viewModel.uiState.value.queens.size)
        assertFalse(BoardPosition(row = 1, column = 0) in viewModel.uiState.value.queens)
    }

    private fun createViewModel(boardSize: Int): GameViewModel = GameViewModel(
        boardSize = boardSize,
        changeQueenPlacement = ChangeQueenPlacement(),
    )
}
