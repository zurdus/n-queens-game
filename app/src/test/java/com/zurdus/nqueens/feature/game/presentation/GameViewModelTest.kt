package com.zurdus.nqueens.feature.game.presentation

import com.zurdus.nqueens.feature.game.domain.model.BoardSquare
import com.zurdus.nqueens.feature.game.domain.usecase.ChangeQueenPlacement
import com.zurdus.nqueens.feature.game.domain.usecase.RestartGame
import com.zurdus.nqueens.feature.game.domain.usecase.UndoLastMove
import com.zurdus.nqueens.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial state uses provided board size`() = runTest {
        val viewModel = createViewModel(boardSize = 10)

        assertEquals(10, viewModel.uiState.value.boardSize)
        assertEquals(10, viewModel.uiState.value.queensLeft)
        assertTrue(viewModel.uiState.value.queenSquares.isEmpty())
    }

    @Test
    fun `clicking a cell toggles its queen`() = runTest {
        val viewModel = createViewModel(boardSize = 4)
        val square = BoardSquare(row = 1, column = 2)

        viewModel.onCellClicked(row = square.row, column = square.column)
        assertEquals(setOf(square), viewModel.uiState.value.queenSquares)

        viewModel.onCellClicked(row = square.row, column = square.column)
        assertTrue(viewModel.uiState.value.queenSquares.isEmpty())
    }

    @Test
    fun `conflicting placements update render state`() = runTest {
        val viewModel = createViewModel(boardSize = 4)
        val firstQueenSquare = BoardSquare(row = 0, column = 0)
        val secondQueenSquare = BoardSquare(row = 0, column = 2)

        viewModel.onCellClicked(firstQueenSquare.row, firstQueenSquare.column)
        viewModel.onCellClicked(secondQueenSquare.row, secondQueenSquare.column)

        assertEquals(
            setOf(firstQueenSquare, secondQueenSquare),
            viewModel.uiState.value.conflictingQueenSquares,
        )
        assertFalse(viewModel.uiState.value.isSolved)
    }

    @Test
    fun `board never accepts more queens than its size`() = runTest {
        val viewModel = createViewModel(boardSize = 4)
        repeat(4) { column -> viewModel.onCellClicked(row = 0, column = column) }

        viewModel.onCellClicked(row = 1, column = 0)

        assertEquals(4, viewModel.uiState.value.queenSquares.size)
        assertFalse(
            BoardSquare(row = 1, column = 0) in viewModel.uiState.value.queenSquares,
        )
    }

    @Test
    fun `undo restores the game before the last placement change`() = runTest {
        val viewModel = createViewModel(boardSize = 4)
        val firstQueenSquare = BoardSquare(row = 0, column = 1)
        val secondQueenSquare = BoardSquare(row = 1, column = 3)
        viewModel.onCellClicked(firstQueenSquare.row, firstQueenSquare.column)
        viewModel.onCellClicked(secondQueenSquare.row, secondQueenSquare.column)

        viewModel.onUndoClicked()

        assertEquals(setOf(firstQueenSquare), viewModel.uiState.value.queenSquares)
        assertTrue(viewModel.uiState.value.canUndo)
    }

    @Test
    fun `reset clears placements and undo history`() = runTest {
        val viewModel = createViewModel(boardSize = 4)
        viewModel.onCellClicked(row = 0, column = 1)
        viewModel.onCellClicked(row = 1, column = 3)

        viewModel.onResetClicked()

        assertTrue(viewModel.uiState.value.queenSquares.isEmpty())
        assertEquals(4, viewModel.uiState.value.queensLeft)
        assertFalse(viewModel.uiState.value.canUndo)
    }

    private fun TestScope.createViewModel(boardSize: Int): GameViewModel {
        val viewModel = GameViewModel(
            boardSize = boardSize,
            changeQueenPlacement = ChangeQueenPlacement(),
            undoLastMove = UndoLastMove(),
            restartGame = RestartGame(),
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        return viewModel
    }
}
