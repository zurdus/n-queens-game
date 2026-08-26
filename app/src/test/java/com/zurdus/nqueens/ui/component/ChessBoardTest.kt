package com.zurdus.nqueens.ui.component

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChessBoardTest {

    @Test
    fun `top-left square is light`() {
        assertTrue(isLightSquare(row = 0, column = 0))
    }

    @Test
    fun `adjacent horizontal and vertical squares alternate`() {
        assertFalse(isLightSquare(row = 0, column = 1))
        assertFalse(isLightSquare(row = 1, column = 0))
        assertTrue(isLightSquare(row = 1, column = 1))
    }

    @Test
    fun `adjacent squares alternate for even and odd board dimensions`() {
        listOf(4, 5).forEach { boardSize ->
            repeat(boardSize) { row ->
                repeat(boardSize) { column ->
                    if (column + 1 < boardSize) {
                        assertNotEquals(
                            isLightSquare(row, column),
                            isLightSquare(row, column + 1),
                        )
                    }
                    if (row + 1 < boardSize) {
                        assertNotEquals(
                            isLightSquare(row, column),
                            isLightSquare(row + 1, column),
                        )
                    }
                }
            }
        }
    }
}
