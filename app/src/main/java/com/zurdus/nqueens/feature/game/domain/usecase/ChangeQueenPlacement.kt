package com.zurdus.nqueens.feature.game.domain.usecase

import com.zurdus.nqueens.feature.game.domain.model.BoardPosition
import com.zurdus.nqueens.feature.game.domain.model.NQueensGame
import com.zurdus.nqueens.feature.game.domain.model.isOnBoard

internal class ChangeQueenPlacement {

    operator fun invoke(
        game: NQueensGame,
        position: BoardPosition,
    ): NQueensGame {
        if (!position.isOnBoard(game.boardSize)) return game

        val updatedQueens = when {
            position in game.queens -> game.queens - position
            game.queensLeft > 0 -> game.queens + position
            else -> game.queens
        }

        return if (updatedQueens == game.queens) {
            game
        } else {
            game.copy(queens = updatedQueens)
        }
    }
}
