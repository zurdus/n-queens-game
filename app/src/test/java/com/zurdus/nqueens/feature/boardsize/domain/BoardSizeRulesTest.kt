package com.zurdus.nqueens.feature.boardsize.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class BoardSizeRulesTest {

    @Test
    fun `clamp keeps every supported board size unchanged`() {
        (BoardSizeRules.MIN..BoardSizeRules.MAX).forEach { size ->
            assertEquals(size, BoardSizeRules.clamp(size))
        }
    }

    @Test
    fun `clamp returns minimum when size is too small`() {
        assertEquals(BoardSizeRules.MIN, BoardSizeRules.clamp(Int.MIN_VALUE))
    }

    @Test
    fun `clamp returns maximum when size is too large`() {
        assertEquals(BoardSizeRules.MAX, BoardSizeRules.clamp(Int.MAX_VALUE))
    }
}
