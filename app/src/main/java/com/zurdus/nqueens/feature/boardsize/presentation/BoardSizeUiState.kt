package com.zurdus.nqueens.feature.boardsize.presentation

import com.zurdus.nqueens.feature.boardsize.domain.BoardSizeRules

internal data class BoardSizeUiState(
    val selectedSize: Int = BoardSizeRules.DEFAULT,
    val minimumSize: Int = BoardSizeRules.MIN,
    val maximumSize: Int = BoardSizeRules.MAX,
)
