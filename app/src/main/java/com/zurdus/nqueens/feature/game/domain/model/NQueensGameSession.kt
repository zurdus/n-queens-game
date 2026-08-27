package com.zurdus.nqueens.feature.game.domain.model

internal data class NQueensGameSession(
    val currentGame: NQueensGame,
    val previousGames: List<NQueensGame> = emptyList(),
) {
    val canUndo: Boolean
        get() = previousGames.isNotEmpty()
}
