package com.zurdus.nqueens.feature.game.presentation

import androidx.lifecycle.ViewModel
import com.zurdus.nqueens.feature.game.domain.model.BoardPosition
import com.zurdus.nqueens.feature.game.domain.model.NQueensGame
import com.zurdus.nqueens.feature.game.domain.usecase.ChangeQueenPlacement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class GameViewModel(
    boardSize: Int,
    private val changeQueenPlacement: ChangeQueenPlacement,
) : ViewModel() {

    private var game = NQueensGame(boardSize = boardSize)
    private val _uiState = MutableStateFlow(game.toUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun onCellClicked(row: Int, column: Int) {
        val updatedGame = changeQueenPlacement(
            game = game,
            position = BoardPosition(row = row, column = column),
        )
        if (updatedGame == game) return

        game = updatedGame
        _uiState.value = updatedGame.toUiState()
    }
}

private fun NQueensGame.toUiState(): GameUiState = GameUiState(
    boardSize = boardSize,
    queens = queens,
    conflictingQueens = conflictingQueens,
    queensLeft = queensLeft,
    isSolved = isSolved,
)
