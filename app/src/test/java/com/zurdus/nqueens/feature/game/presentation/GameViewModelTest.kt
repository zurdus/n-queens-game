package com.zurdus.nqueens.feature.game.presentation

import org.junit.Assert.assertEquals
import org.junit.Test

class GameViewModelTest {

    @Test
    fun `initial state uses provided board size`() {
        val viewModel = GameViewModel(boardSize = 10)

        assertEquals(
            GameUiState(boardSize = 10),
            viewModel.uiState.value,
        )
    }
}
