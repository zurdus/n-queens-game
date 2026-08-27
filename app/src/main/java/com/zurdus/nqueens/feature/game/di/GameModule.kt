package com.zurdus.nqueens.feature.game.di

import com.zurdus.nqueens.feature.game.domain.usecase.ChangeQueenPlacement
import com.zurdus.nqueens.feature.game.domain.usecase.RestartGame
import com.zurdus.nqueens.feature.game.domain.usecase.UndoLastMove
import com.zurdus.nqueens.feature.game.presentation.GameViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal val gameModule = module {
    factoryOf(::ChangeQueenPlacement)

    factoryOf(::UndoLastMove)

    factoryOf(::RestartGame)

    viewModel { parameters ->
        GameViewModel(
            boardSize = parameters.get(),
            changeQueenPlacement = get(),
            undoLastMove = get(),
            restartGame = get(),
        )
    }
}
