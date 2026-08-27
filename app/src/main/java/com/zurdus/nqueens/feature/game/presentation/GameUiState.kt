package com.zurdus.nqueens.feature.game.presentation

import com.zurdus.nqueens.feature.game.domain.model.BoardSquare

internal data class GameUiState(
    val boardSize: Int,
    val queenSquares: Set<BoardSquare> = emptySet(),
    val conflictingQueenSquares: Set<BoardSquare> = emptySet(),
    val queensLeft: Int = boardSize,
    val isSolved: Boolean = false,
    val canUndo: Boolean = false,
)
