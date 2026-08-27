package com.zurdus.nqueens.feature.game.domain.usecase

import com.zurdus.nqueens.feature.game.domain.model.BoardSquare
import com.zurdus.nqueens.feature.game.domain.model.NQueensGameSession
import com.zurdus.nqueens.feature.game.domain.model.isOnBoard

internal class ChangeQueenPlacement {

    operator fun invoke(
        session: NQueensGameSession,
        square: BoardSquare,
    ): NQueensGameSession {
        val game = session.currentGame
        if (!square.isOnBoard(game.boardSize)) return session

        val updatedQueenSquares = when {
            square in game.queenSquares -> game.queenSquares - square
            game.queensLeft > 0 -> game.queenSquares + square
            else -> game.queenSquares
        }

        if (updatedQueenSquares == game.queenSquares) return session

        return session.copy(
            currentGame = game.copy(queenSquares = updatedQueenSquares),
            previousGames = session.previousGames + game,
        )
    }
}
