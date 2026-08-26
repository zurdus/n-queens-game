package com.zurdus.nqueens.feature.game.navigation

import kotlinx.serialization.Serializable

internal sealed interface GameDestination {

    @Serializable
    data class Board(val boardSize: Int) : GameDestination
}
