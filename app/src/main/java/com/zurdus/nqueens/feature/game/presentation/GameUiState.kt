package com.zurdus.nqueens.feature.game.presentation

import com.zurdus.nqueens.feature.game.domain.model.BoardPosition

internal data class GameUiState(
    val boardSize: Int,
    val queens: Set<BoardPosition> = emptySet(),
    val conflictingQueens: Set<BoardPosition> = emptySet(),
    val queensLeft: Int = boardSize,
    val isSolved: Boolean = false,
)
