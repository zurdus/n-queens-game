package com.zurdus.nqueens.feature.boardsize.presentation

import com.zurdus.nqueens.domain.NQueensRules

internal data class BoardSizeUiState(
    val selectedSize: Int = NQueensRules.DEFAULT_BOARD_SIZE,
    val minimumSize: Int = NQueensRules.MINIMUM_BOARD_SIZE,
    val maximumSize: Int = NQueensRules.MAXIMUM_BOARD_SIZE,
)
