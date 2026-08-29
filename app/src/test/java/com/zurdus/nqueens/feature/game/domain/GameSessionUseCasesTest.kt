package com.zurdus.nqueens.feature.game.domain

import com.zurdus.nqueens.feature.game.domain.model.BoardSquare
import com.zurdus.nqueens.feature.game.domain.model.GameSession
import com.zurdus.nqueens.feature.game.domain.model.Position
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
        val initialPosition = Position(boardSize = 4)
        val initialSession = GameSession(currentPosition = initialPosition)
        val square = BoardSquare(row = 1, column = 2)

        val sessionWithQueen = changeQueenPlacement(initialSession, square)
        val emptySessionAgain = changeQueenPlacement(sessionWithQueen, square)

        assertEquals(setOf(square), sessionWithQueen.currentPosition.queenSquares)
        assertEquals(listOf(initialPosition), sessionWithQueen.previousPositions)
        assertTrue(emptySessionAgain.currentPosition.queenSquares.isEmpty())
        assertEquals(
            listOf(initialPosition, sessionWithQueen.currentPosition),
            emptySessionAgain.previousPositions,
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
        val fullPosition = Position(
            boardSize = 4,
            queenSquares = setOf(
                BoardSquare(0, 0),
                BoardSquare(0, 1),
                BoardSquare(0, 2),
                BoardSquare(0, 3),
            ),
        )
        val session = GameSession(currentPosition = fullPosition)

        val unchangedSession = changeQueenPlacement(
            session = session,
            square = BoardSquare(row = 1, column = 0),
        )

        assertSame(session, unchangedSession)
    }

    @Test
    fun `undo restores the last position and removes it from history`() {
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

        assertEquals(firstMove.currentPosition, undoneSession.currentPosition)
        assertEquals(firstMove.previousPositions, undoneSession.previousPositions)
        assertTrue(undoneSession.canUndo)
    }

    @Test
    fun `undo without history leaves the session unchanged`() {
        val session = emptySession()

        assertSame(session, undoLastMove(session))
    }

    @Test
    fun `restart clears the current position and its history`() {
        val sessionWithQueen = changeQueenPlacement(
            emptySession(),
            BoardSquare(row = 0, column = 1),
        )

        val restartedSession = restartGame(sessionWithQueen)

        assertTrue(restartedSession.currentPosition.queenSquares.isEmpty())
        assertEquals(4, restartedSession.currentPosition.boardSize)
        assertTrue(restartedSession.previousPositions.isEmpty())
        assertFalse(restartedSession.canUndo)
    }

    private fun emptySession() = GameSession(
        currentPosition = Position(boardSize = 4),
    )
}
