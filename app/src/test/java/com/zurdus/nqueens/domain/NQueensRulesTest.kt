package com.zurdus.nqueens.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NQueensRulesTest {

    @Test
    fun `supported board sizes include every size from minimum through maximum`() {
        assertEquals(
            NQueensRules.MINIMUM_BOARD_SIZE..NQueensRules.MAXIMUM_BOARD_SIZE,
            NQueensRules.supportedBoardSizes,
        )
    }

    @Test
    fun `default board size is supported`() {
        assertTrue(NQueensRules.DEFAULT_BOARD_SIZE in NQueensRules.supportedBoardSizes)
    }
}
