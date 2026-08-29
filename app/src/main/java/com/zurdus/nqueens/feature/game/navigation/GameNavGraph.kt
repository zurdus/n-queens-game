package com.zurdus.nqueens.feature.game.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.zurdus.nqueens.feature.game.presentation.GameScreen
import kotlinx.serialization.Serializable

@Serializable
internal data class GameNavGraph(val boardSize: Int)

internal fun NavGraphBuilder.gameNavGraph(
    navController: NavController,
) {
    navigation<GameNavGraph>(
        startDestination = GameDestination.Board,
    ) {
        composable<GameDestination.Board> { backStackEntry ->
            val gameGraphEntry = remember(backStackEntry) {
                navController.getBackStackEntry<GameNavGraph>()
            }
            val gameGraph = gameGraphEntry.toRoute<GameNavGraph>()

            GameScreen(
                boardSize = gameGraph.boardSize,
                onNavigateBack = navController::navigateUp,
            )
        }
    }
}
