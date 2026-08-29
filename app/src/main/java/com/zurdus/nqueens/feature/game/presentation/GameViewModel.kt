package com.zurdus.nqueens.feature.game.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zurdus.nqueens.feature.game.domain.model.BoardSquare
import com.zurdus.nqueens.feature.game.domain.model.GameSession
import com.zurdus.nqueens.feature.game.domain.model.Position
import com.zurdus.nqueens.feature.game.domain.usecase.ChangeQueenPlacement
import com.zurdus.nqueens.feature.game.domain.usecase.RestartGame
import com.zurdus.nqueens.feature.game.domain.usecase.UndoLastMove
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

internal class GameViewModel(
    boardSize: Int,
    private val changeQueenPlacement: ChangeQueenPlacement,
    private val undoLastMove: UndoLastMove,
    private val restartGame: RestartGame,
) : ViewModel() {

    private val sessionState = MutableStateFlow(
        GameSession(
            currentPosition = Position(boardSize = boardSize),
        ),
    )
    val uiState: StateFlow<GameUiState> = sessionState
        .map(GameSession::toUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = sessionState.value.toUiState(),
        )

    fun onCellClicked(row: Int, column: Int) {
        sessionState.update { session ->
            changeQueenPlacement(
                session = session,
                square = BoardSquare(row = row, column = column),
            )
        }
    }

    fun onUndoClicked() {
        sessionState.update { session -> undoLastMove(session) }
    }

    fun onResetClicked() {
        sessionState.update { session -> restartGame(session) }
    }
}

private fun GameSession.toUiState(): GameUiState {
    val position = currentPosition

    return GameUiState(
        boardSize = position.boardSize,
        queenSquares = position.queenSquares,
        conflictingQueenSquares = position.conflictingQueenSquares,
        queensLeft = position.queensLeft,
        isSolved = position.isSolved,
        canUndo = canUndo,
    )
}
