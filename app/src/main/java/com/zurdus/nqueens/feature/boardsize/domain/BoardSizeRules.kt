package com.zurdus.nqueens.feature.boardsize.domain

internal object BoardSizeRules {
    const val MIN = 4
    const val DEFAULT = 8
    const val MAX = 12

    fun clamp(size: Int): Int = size.coerceIn(MIN, MAX)
}
