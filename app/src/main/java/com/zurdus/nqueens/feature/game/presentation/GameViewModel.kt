package com.zurdus.nqueens.feature.game.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class GameViewModel(boardSize: Int) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState(boardSize = boardSize))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
}
