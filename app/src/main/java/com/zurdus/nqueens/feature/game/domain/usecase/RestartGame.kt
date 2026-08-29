package com.zurdus.nqueens.feature.game.domain.usecase

import com.zurdus.nqueens.feature.game.domain.model.GameSession
import com.zurdus.nqueens.feature.game.domain.model.Position

internal class RestartGame {

    operator fun invoke(session: GameSession): GameSession =
        GameSession(
            currentPosition = Position(boardSize = session.currentPosition.boardSize),
        )
}
