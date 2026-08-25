package com.zurdus.nqueens.feature.boardsize.presentation

import androidx.lifecycle.ViewModel
import com.zurdus.nqueens.feature.boardsize.domain.BoardSizeRules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class BoardSizeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BoardSizeUiState())
    val uiState: StateFlow<BoardSizeUiState> = _uiState.asStateFlow()

    fun onBoardSizeChanged(size: Int) {
        val selectedSize = BoardSizeRules.clamp(size)
        if (selectedSize == _uiState.value.selectedSize) return

        _uiState.update { currentState ->
            currentState.copy(selectedSize = selectedSize)
        }
    }
}
