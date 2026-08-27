package com.zurdus.nqueens.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.zurdus.nqueens.feature.boardsize.navigation.BoardSizeGraph
import com.zurdus.nqueens.feature.boardsize.navigation.boardSizeNavGraph
import com.zurdus.nqueens.feature.game.navigation.gameNavGraph
import com.zurdus.nqueens.feature.game.navigation.navigateToGame

@Composable
fun NQueensNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = BoardSizeGraph,
        modifier = modifier,
    ) {
        boardSizeNavGraph(
            onStartGame = navController::navigateToGame,
        )
        gameNavGraph(navController)
    }
}
