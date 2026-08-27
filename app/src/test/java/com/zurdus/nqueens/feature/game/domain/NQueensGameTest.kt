package com.zurdus.nqueens.feature.game.domain

import com.zurdus.nqueens.feature.game.domain.model.BoardSquare
import com.zurdus.nqueens.feature.game.domain.model.NQueensGame
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NQueensGameTest {

    @Test
    fun `new game starts empty with every queen left to place`() {
        val game = NQueensGame(boardSize = 8)

        assertTrue(game.queenSquares.isEmpty())
        assertEquals(8, game.queensLeft)
        assertFalse(game.isSolved)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `board smaller than four is rejected`() {
        NQueensGame(boardSize = 3)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `board larger than the supported maximum is rejected`() {
        NQueensGame(boardSize = 13)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `queen outside the board is rejected`() {
        NQueensGame(
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
            val game = NQueensGame(
                boardSize = 4,
                queenSquares = setOf(
                    BoardSquare(row = 0, column = 0),
                    secondQueenSquare,
                ),
            )

            assertEquals(game.queenSquares, game.conflictingQueenSquares)
            assertFalse(game.isSolved)
        }
    }

    @Test
    fun `valid complete board is solved`() {
        val game = NQueensGame(
            boardSize = 4,
            queenSquares = setOf(
                BoardSquare(row = 0, column = 1),
                BoardSquare(row = 1, column = 3),
                BoardSquare(row = 2, column = 0),
                BoardSquare(row = 3, column = 2),
            ),
        )

        assertTrue(game.conflictingQueenSquares.isEmpty())
        assertEquals(0, game.queensLeft)
        assertTrue(game.isSolved)
    }

}
