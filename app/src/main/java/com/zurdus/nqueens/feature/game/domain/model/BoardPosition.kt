package com.zurdus.nqueens.feature.game.domain.model

internal data class BoardPosition(
    val row: Int,
    val column: Int,
)

internal fun BoardPosition.isOnBoard(boardSize: Int): Boolean =
    row in 0 until boardSize && column in 0 until boardSize

internal fun BoardPosition.attacks(other: BoardPosition): Boolean =
    row == other.row ||
        column == other.column ||
        kotlin.math.abs(row - other.row) == kotlin.math.abs(column - other.column)
