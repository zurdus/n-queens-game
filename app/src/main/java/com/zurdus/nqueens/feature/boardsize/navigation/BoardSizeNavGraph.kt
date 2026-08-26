package com.zurdus.nqueens.feature.boardsize.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.zurdus.nqueens.feature.boardsize.presentation.BoardSizeScreen
import kotlinx.serialization.Serializable

@Serializable
internal data object BoardSizeGraph

internal fun NavGraphBuilder.boardSizeNavGraph(
    navController: NavController,
    onStartGame: (Int) -> Unit,
) {
    navigation<BoardSizeGraph>(
        startDestination = BoardSizeDestination.Selection,
    ) {
        composable<BoardSizeDestination.Selection> {
            BoardSizeScreen(onStartGame = onStartGame)
        }
    }
}
