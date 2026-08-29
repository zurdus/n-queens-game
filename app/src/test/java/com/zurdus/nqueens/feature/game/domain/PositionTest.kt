package com.zurdus.nqueens.feature.game.domain

import com.zurdus.nqueens.feature.game.domain.model.BoardSquare
import com.zurdus.nqueens.feature.game.domain.model.Position
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PositionTest {

    @Test
    fun `new position starts empty with every queen left to place`() {
        val position = Position(boardSize = 8)

        assertTrue(position.queenSquares.isEmpty())
        assertEquals(8, position.queensLeft)
        assertFalse(position.isSolved)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `board smaller than four is rejected`() {
        Position(boardSize = 3)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `board larger than the supported maximum is rejected`() {
        Position(boardSize = 13)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `queen outside the board is rejected`() {
        Position(
            boardSize = 4,
            queenSquares = setOf(BoardSquare(row = 4, column = 0)),
        )
    }

    @Test
    fun `row column and diagonal attacks mark both queens as conflicting`() {
        listOf(
            BoardSquare(row = 0, column = 3),
            BoardSquare(row = 3, column = 0),
            BoardSquare(row = 2, column = 2),
        ).forEach { secondQueenSquare ->
            val position = Position(
                boardSize = 4,
                queenSquares = setOf(
                    BoardSquare(row = 0, column = 0),
                    secondQueenSquare,
                ),
            )

            assertEquals(position.queenSquares, position.conflictingQueenSquares)
            assertFalse(position.isSolved)
        }
    }

    @Test
    fun `valid complete board is solved`() {
        val position = Position(
            boardSize = 4,
            queenSquares = setOf(
                BoardSquare(row = 0, column = 1),
                BoardSquare(row = 1, column = 3),
                BoardSquare(row = 2, column = 0),
                BoardSquare(row = 3, column = 2),
            ),
        )

        assertTrue(position.conflictingQueenSquares.isEmpty())
        assertEquals(0, position.queensLeft)
        assertTrue(position.isSolved)
    }

}
