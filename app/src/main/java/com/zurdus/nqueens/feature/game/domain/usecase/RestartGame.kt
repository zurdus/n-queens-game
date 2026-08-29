package com.zurdus.nqueens.feature.game.domain.usecase

import com.zurdus.nqueens.feature.game.domain.model.NQueensGame
import com.zurdus.nqueens.feature.game.domain.model.NQueensGameSession

internal class RestartGame {

    operator fun invoke(session: NQueensGameSession): NQueensGameSession =
        NQueensGameSession(
            currentGame = NQueensGame(boardSize = session.currentGame.boardSize),
        )
}
