package com.zurdus.nqueens.feature.game.domain

import com.zurdus.nqueens.feature.game.domain.model.BoardSquare
import com.zurdus.nqueens.feature.game.domain.model.NQueensGame
import com.zurdus.nqueens.feature.game.domain.model.NQueensGameSession
import com.zurdus.nqueens.feature.game.domain.usecase.ChangeQueenPlacement
import com.zurdus.nqueens.feature.game.domain.usecase.RestartGame
import com.zurdus.nqueens.feature.game.domain.usecase.UndoLastMove
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GameSessionUseCasesTest {

    private val changeQueenPlacement = ChangeQueenPlacement()
    private val undoLastMove = UndoLastMove()
    private val restartGame = RestartGame()

    @Test
    fun `placement adds and removes a queen while recording each change`() {
        val initialGame = NQueensGame(boardSize = 4)
        val initialSession = NQueensGameSession(currentGame = initialGame)
        val square = BoardSquare(row = 1, column = 2)

        val sessionWithQueen = changeQueenPlacement(initialSession, square)
        val emptySessionAgain = changeQueenPlacement(sessionWithQueen, square)

        assertEquals(setOf(square), sessionWithQueen.currentGame.queenSquares)
        assertEquals(listOf(initialGame), sessionWithQueen.previousGames)
        assertTrue(emptySessionAgain.currentGame.queenSquares.isEmpty())
        assertEquals(
            listOf(initialGame, sessionWithQueen.currentGame),
            emptySessionAgain.previousGames,
        )
    }

    @Test
    fun `placement outside the board leaves the session unchanged`() {
        val session = emptySession()

        val unchangedSession = changeQueenPlacement(
            session = session,
            square = BoardSquare(row = 4, column = 0),
        )

        assertSame(session, unchangedSession)
    }

    @Test
    fun `placement never adds more queens than the board size`() {
        val fullGame = NQueensGame(
            boardSize = 4,
            queenSquares = setOf(
                BoardSquare(0, 0),
                BoardSquare(0, 1),
                BoardSquare(0, 2),
                BoardSquare(0, 3),
            ),
        )
        val session = NQueensGameSession(currentGame = fullGame)

        val unchangedSession = changeQueenPlacement(
            session = session,
            square = BoardSquare(row = 1, column = 0),
        )

        assertSame(session, unchangedSession)
    }

    @Test
    fun `undo restores the last game and removes it from history`() {
        val initialSession = emptySession()
        val firstMove = changeQueenPlacement(
            initialSession,
            BoardSquare(row = 0, column = 1),
        )
        val secondMove = changeQueenPlacement(
            firstMove,
            BoardSquare(row = 1, column = 3),
        )

        val undoneSession = undoLastMove(secondMove)

        assertEquals(firstMove.currentGame, undoneSession.currentGame)
        assertEquals(firstMove.previousGames, undoneSession.previousGames)
        assertTrue(undoneSession.canUndo)
    }

    @Test
    fun `undo without history leaves the session unchanged`() {
        val session = emptySession()

        assertSame(session, undoLastMove(session))
    }

    @Test
    fun `restart clears the current game and its history`() {
        val sessionWithQueen = changeQueenPlacement(
            emptySession(),
            BoardSquare(row = 0, column = 1),
        )

        val restartedSession = restartGame(sessionWithQueen)

        assertTrue(restartedSession.currentGame.queenSquares.isEmpty())
        assertEquals(4, restartedSession.currentGame.boardSize)
        assertTrue(restartedSession.previousGames.isEmpty())
        assertFalse(restartedSession.canUndo)
    }

    private fun emptySession() = NQueensGameSession(
        currentGame = NQueensGame(boardSize = 4),
    )
}
