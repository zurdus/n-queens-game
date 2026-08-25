package com.zurdus.nqueens.feature.boardsize.navigation

import kotlinx.serialization.Serializable

internal sealed interface BoardSizeDestination {

    @Serializable
    data object Selection : BoardSizeDestination
}
