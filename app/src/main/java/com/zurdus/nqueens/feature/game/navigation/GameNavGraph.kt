package com.zurdus.nqueens.feature.game.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.zurdus.nqueens.feature.game.presentation.GameScreen
import kotlinx.serialization.Serializable

@Serializable
internal data object GameNavGraph

internal fun NavGraphBuilder.gameNavGraph(
    navController: NavController,
) {
    navigation<GameNavGraph>(
        startDestination = GameDestination.Board::class,
    ) {
        composable<GameDestination.Board> { backStackEntry ->
            val destination = backStackEntry.toRoute<GameDestination.Board>()

            GameScreen(
                boardSize = destination.boardSize,
            )
        }
    }
}

internal fun NavController.navigateToGame(boardSize: Int) {
    navigate(GameDestination.Board(boardSize = boardSize)) {
        launchSingleTop = true
    }
}
