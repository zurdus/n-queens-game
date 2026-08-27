package com.zurdus.nqueens.feature.game.domain.usecase

import com.zurdus.nqueens.feature.game.domain.model.NQueensGameSession

internal class UndoLastMove {

    operator fun invoke(session: NQueensGameSession): NQueensGameSession {
        val previousGame = session.previousGames.lastOrNull() ?: return session

        return session.copy(
            currentGame = previousGame,
            previousGames = session.previousGames.dropLast(1),
        )
    }
}
