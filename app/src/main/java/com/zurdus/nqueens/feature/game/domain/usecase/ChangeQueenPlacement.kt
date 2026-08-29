package com.zurdus.nqueens.feature.game.domain.usecase

import com.zurdus.nqueens.feature.game.domain.model.BoardSquare
import com.zurdus.nqueens.feature.game.domain.model.GameSession
import com.zurdus.nqueens.feature.game.domain.model.isOnBoard

internal class ChangeQueenPlacement {

    operator fun invoke(
        session: GameSession,
        square: BoardSquare,
    ): GameSession {
        val position = session.currentPosition
        if (!square.isOnBoard(position.boardSize)) return session

        val updatedQueenSquares = when {
            square in position.queenSquares -> position.queenSquares - square
            position.queensLeft > 0 -> position.queenSquares + square
            else -> position.queenSquares
        }

        if (updatedQueenSquares == position.queenSquares) return session

        return session.copy(
            currentPosition = position.copy(queenSquares = updatedQueenSquares),
            previousPositions = session.previousPositions + position,
        )
    }
}
