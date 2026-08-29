package com.zurdus.nqueens.feature.game.domain.model

internal data class GameSession(
    val currentPosition: Position,
    val previousPositions: List<Position> = emptyList(),
) {
    val canUndo: Boolean
        get() = previousPositions.isNotEmpty()
}
