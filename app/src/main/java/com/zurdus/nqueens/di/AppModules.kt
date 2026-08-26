package com.zurdus.nqueens.di

import com.zurdus.nqueens.feature.boardsize.di.boardSizeModule
import com.zurdus.nqueens.feature.game.di.gameModule

internal val appModules = listOf(
    boardSizeModule,
    gameModule,
)
