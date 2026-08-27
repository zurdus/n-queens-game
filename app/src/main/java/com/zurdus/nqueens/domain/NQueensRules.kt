package com.zurdus.nqueens.domain

internal object NQueensRules {
    const val MINIMUM_BOARD_SIZE = 4
    const val DEFAULT_BOARD_SIZE = 8
    const val MAXIMUM_BOARD_SIZE = 12

    val supportedBoardSizes = MINIMUM_BOARD_SIZE..MAXIMUM_BOARD_SIZE
}
