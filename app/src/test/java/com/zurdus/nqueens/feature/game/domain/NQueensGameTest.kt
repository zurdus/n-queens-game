package com.zurdus.nqueens.feature.game.domain

import com.zurdus.nqueens.feature.game.domain.model.BoardPosition
import com.zurdus.nqueens.feature.game.domain.model.NQueensGame
import com.zurdus.nqueens.feature.game.domain.usecase.ChangeQueenPlacement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class NQueensGameTest {

    private val changeQueenPlacement = ChangeQueenPlacement()

    @Test
    fun `new game starts empty with every queen left to place`() {
        val game = NQueensGame(boardSize = 8)

        assertTrue(game.queens.isEmpty())
        assertEquals(8, game.queensLeft)
        assertFalse(game.isSolved)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `board smaller than four is rejected`() {
        NQueensGame(boardSize = 3)
    }

    @Test
    fun `row column and diagonal attacks mark both queens as conflicting`() {
        listOf(
            BoardPosition(row = 0, column = 3),
            BoardPosition(row = 3, column = 0),
            BoardPosition(row = 2, column = 2),
        ).forEach { secondQueen ->
            val game = NQueensGame(
                boardSize = 4,
                queens = setOf(BoardPosition(row = 0, column = 0), secondQueen),
            )

            assertEquals(game.queens, game.conflictingQueens)
            assertFalse(game.isSolved)
        }
    }

    @Test
    fun `valid complete board is solved`() {
        val game = NQueensGame(
            boardSize = 4,
            queens = setOf(
                BoardPosition(row = 0, column = 1),
                BoardPosition(row = 1, column = 3),
                BoardPosition(row = 2, column = 0),
                BoardPosition(row = 3, column = 2),
            ),
        )

        assertTrue(game.conflictingQueens.isEmpty())
        assertEquals(0, game.queensLeft)
        assertTrue(game.isSolved)
    }

    @Test
    fun `placement use case adds and removes a queen`() {
        val emptyGame = NQueensGame(boardSize = 4)
        val position = BoardPosition(row = 1, column = 2)

        val gameWithQueen = changeQueenPlacement(emptyGame, position)
        val emptyGameAgain = changeQueenPlacement(gameWithQueen, position)

        assertEquals(setOf(position), gameWithQueen.queens)
        assertTrue(emptyGameAgain.queens.isEmpty())
    }

    @Test
    fun `placement use case ignores positions outside the board`() {
        val game = NQueensGame(boardSize = 4)

        val unchangedGame = changeQueenPlacement(
            game = game,
            position = BoardPosition(row = 4, column = 0),
        )

        assertSame(game, unchangedGame)
    }

    @Test
    fun `placement use case never adds more queens than the board size`() {
        val fullGame = NQueensGame(
            boardSize = 4,
            queens = setOf(
                BoardPosition(0, 0),
                BoardPosition(0, 1),
                BoardPosition(0, 2),
                BoardPosition(0, 3),
            ),
        )

        val unchangedGame = changeQueenPlacement(
            game = fullGame,
            position = BoardPosition(row = 1, column = 0),
        )

        assertSame(fullGame, unchangedGame)
    }
}
