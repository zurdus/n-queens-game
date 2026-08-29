package com.zurdus.nqueens.feature.game.domain.usecase

import com.zurdus.nqueens.feature.game.domain.model.GameSession

internal class UndoLastMove {

    operator fun invoke(session: GameSession): GameSession {
        val previousPosition = session.previousPositions.lastOrNull() ?: return session

        return session.copy(
            currentPosition = previousPosition,
            previousPositions = session.previousPositions.dropLast(1),
        )
    }
}
