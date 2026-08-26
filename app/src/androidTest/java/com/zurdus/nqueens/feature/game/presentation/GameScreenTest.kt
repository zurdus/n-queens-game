package com.zurdus.nqueens.feature.game.presentation

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.semantics.SemanticsActions
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zurdus.nqueens.MainActivity
import com.zurdus.nqueens.R
import com.zurdus.nqueens.feature.boardsize.presentation.BOARD_SIZE_SELECTED_VALUE_TEST_TAG
import com.zurdus.nqueens.feature.boardsize.presentation.BOARD_SIZE_SLIDER_TEST_TAG
import com.zurdus.nqueens.feature.boardsize.presentation.BOARD_SIZE_START_GAME_TEST_TAG
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun usePortraitOrientation() {
        composeRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.activity.resources.configuration.orientation ==
                Configuration.ORIENTATION_PORTRAIT
        }
        composeRule.waitForIdle()
    }

    @Test
    fun defaultSelectionStartsEightQueenGame() {
        startGame()

        composeRule
            .onNodeWithTag(GAME_INSTRUCTION_TEST_TAG)
            .assertIsDisplayed()
            .assertTextEquals(gameInstruction(boardSize = 8))
        composeRule
            .onNodeWithTag(GAME_CHESS_BOARD_TEST_TAG)
            .assertContentDescriptionEquals(gameBoardDescription(boardSize = 8))

        assertBoardCellCount(expectedCount = 64)
        assertGameBoardIsSquare()
        composeRule
            .onNodeWithTag(GAME_PROGRESS_TEST_TAG)
            .assertTextEquals(gameProgress(placed = 0, boardSize = 8))
    }

    @Test
    fun selectedSizeReachesGameAndBackRestoresSelection() {
        selectBoardSize(size = 4)
        startGame()

        composeRule
            .onNodeWithTag(GAME_INSTRUCTION_TEST_TAG)
            .assertTextEquals(gameInstruction(boardSize = 4))
        composeRule
            .onAllNodesWithText(boardSizeLabel(size = 4))
            .assertCountEquals(0)
        assertBoardCellCount(expectedCount = 16)

        pressBack()
        composeRule.waitForIdle()

        composeRule
            .onNodeWithTag(BOARD_SIZE_SELECTED_VALUE_TEST_TAG)
            .assertTextEquals(boardSizeLabel(size = 4))
        assertBoardCellCount(expectedCount = 16)
    }

    @Test
    fun placingConflictingQueensUpdatesProgressAndUndoResetControls() {
        startGame()

        clickCell(row = 0, column = 0)
        clickCell(row = 0, column = 2)
        composeRule
            .onNodeWithTag(GAME_PROGRESS_TEST_TAG)
            .assertTextEquals(gameProgress(placed = 2, boardSize = 8))
        composeRule
            .onAllNodesWithText(composeRule.activity.getString(R.string.game_status_conflict_title))
            .assertCountEquals(1)

        composeRule.onNodeWithTag(GAME_UNDO_TEST_TAG).performClick()
        composeRule
            .onNodeWithTag(GAME_PROGRESS_TEST_TAG)
            .assertTextEquals(gameProgress(placed = 1, boardSize = 8))

        composeRule.onNodeWithTag(GAME_RESET_TEST_TAG).performClick()
        composeRule
            .onNodeWithTag(GAME_PROGRESS_TEST_TAG)
            .assertTextEquals(gameProgress(placed = 0, boardSize = 8))
    }

    private fun selectBoardSize(size: Int) {
        composeRule
            .onNodeWithTag(BOARD_SIZE_SLIDER_TEST_TAG)
            .performSemanticsAction(SemanticsActions.SetProgress) { setProgress ->
                setProgress(size.toFloat())
            }
        composeRule.waitForIdle()
    }

    private fun startGame() {
        composeRule
            .onNodeWithTag(BOARD_SIZE_START_GAME_TEST_TAG)
            .performClick()
        composeRule.waitForIdle()
    }

    private fun assertBoardCellCount(expectedCount: Int) {
        composeRule
            .onAllNodes(CHESS_BOARD_CELL_MATCHER, useUnmergedTree = true)
            .assertCountEquals(expectedCount)
    }

    private fun assertGameBoardIsSquare() {
        val boardBounds = composeRule
            .onNodeWithTag(GAME_CHESS_BOARD_TEST_TAG)
            .getUnclippedBoundsInRoot()

        assertEquals(
            "Game board width and height should match.",
            (boardBounds.right - boardBounds.left).value,
            (boardBounds.bottom - boardBounds.top).value,
            0.5f,
        )
    }

    private fun gameInstruction(boardSize: Int): String =
        composeRule.activity.getString(R.string.game_instruction, boardSize)

    private fun gameProgress(placed: Int, boardSize: Int): String =
        composeRule.activity.getString(R.string.game_progress, placed, boardSize)

    private fun gameBoardDescription(boardSize: Int): String =
        composeRule.activity.getString(
            R.string.game_board_content_description,
            boardSize,
            boardSize,
        )

    private fun boardSizeLabel(size: Int): String =
        composeRule.activity.getString(R.string.board_size_dimension, size, size)

    private fun clickCell(row: Int, column: Int) {
        composeRule
            .onNodeWithTag("chess_board_cell_${row}_$column", useUnmergedTree = true)
            .performClick()
        composeRule.waitForIdle()
    }

    private companion object {
        val CHESS_BOARD_CELL_MATCHER = SemanticsMatcher("Chessboard cell test tag") { node ->
            SemanticsProperties.TestTag in node.config &&
                node.config[SemanticsProperties.TestTag].startsWith("chess_board_cell_")
        }
    }
}
