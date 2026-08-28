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
import androidx.compose.ui.test.onAllNodesWithTag
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
import org.junit.Assert.assertTrue
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
            .assertTextEquals(queensLeftLabel(queensLeft = 8))

        assertStatusIsAboveBoardAndProgressIsBelowBoard()
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
            .assertTextEquals(queensLeftLabel(queensLeft = 6))
        composeRule
            .onAllNodesWithText(composeRule.activity.getString(R.string.game_status_conflict_title))
            .assertCountEquals(1)

        composeRule.onNodeWithTag(GAME_UNDO_TEST_TAG).performClick()
        composeRule
            .onNodeWithTag(GAME_PROGRESS_TEST_TAG)
            .assertTextEquals(queensLeftLabel(queensLeft = 7))
        composeRule
            .onAllNodesWithText(composeRule.activity.getString(R.string.game_status_safe_message))
            .assertCountEquals(1)

        composeRule.onNodeWithTag(GAME_RESET_TEST_TAG).performClick()
        composeRule
            .onNodeWithTag(GAME_PROGRESS_TEST_TAG)
            .assertTextEquals(queensLeftLabel(queensLeft = 8))
    }

    @Test
    fun progressUsesSingularCopyWhenOneQueenRemains() {
        startFourByFourGame()

        clickCell(row = 0, column = 0)
        clickCell(row = 1, column = 1)
        clickCell(row = 2, column = 2)

        composeRule
            .onNodeWithTag(GAME_PROGRESS_TEST_TAG)
            .assertTextEquals(queensLeftLabel(queensLeft = 1))
    }

    @Test
    fun solvingFourByFourShowsVictoryAndCloseRevealsSolution() {
        startFourByFourGame()
        solveFourByFourGame()

        composeRule
            .onNodeWithTag(GAME_VICTORY_DIALOG_TEST_TAG)
            .assertIsDisplayed()
        composeRule
            .onNodeWithTag(GAME_VICTORY_TITLE_TEST_TAG)
            .assertTextEquals(composeRule.activity.getString(R.string.game_status_solved_title))
        composeRule
            .onNodeWithTag(GAME_VICTORY_MESSAGE_TEST_TAG)
            .assertTextEquals(gameSolvedMessage(boardSize = 4))

        composeRule.onNodeWithTag(GAME_VICTORY_CLOSE_TEST_TAG).performClick()
        composeRule.waitForIdle()

        composeRule
            .onAllNodesWithTag(GAME_VICTORY_DIALOG_TEST_TAG)
            .assertCountEquals(0)
        composeRule
            .onNodeWithTag(GAME_CHESS_BOARD_TEST_TAG)
            .assertIsDisplayed()
        composeRule
            .onNodeWithTag(GAME_PROGRESS_TEST_TAG)
            .assertTextEquals(queensLeftLabel(queensLeft = 0))
        composeRule
            .onAllNodesWithText(composeRule.activity.getString(R.string.game_status_solved_title))
            .assertCountEquals(1)
    }

    @Test
    fun playAgainFromVictoryRestartsTheSameBoardSize() {
        startFourByFourGame()
        solveFourByFourGame()

        composeRule.onNodeWithTag(GAME_VICTORY_PLAY_AGAIN_TEST_TAG).performClick()
        composeRule.waitForIdle()

        composeRule
            .onAllNodesWithTag(GAME_VICTORY_DIALOG_TEST_TAG)
            .assertCountEquals(0)
        composeRule
            .onNodeWithTag(GAME_PROGRESS_TEST_TAG)
            .assertTextEquals(queensLeftLabel(queensLeft = 4))
        assertBoardCellCount(expectedCount = 16)
    }

    @Test
    fun chooseBoardSizeFromVictoryReturnsToRetainedSelection() {
        startFourByFourGame()
        solveFourByFourGame()

        composeRule.onNodeWithTag(GAME_VICTORY_CHOOSE_SIZE_TEST_TAG).performClick()
        composeRule.waitForIdle()

        composeRule
            .onNodeWithTag(BOARD_SIZE_SELECTED_VALUE_TEST_TAG)
            .assertTextEquals(boardSizeLabel(size = 4))
        assertBoardCellCount(expectedCount = 16)
    }

    @Test
    fun systemBackFromVictoryRevealsSolutionBeforeLeavingGame() {
        startFourByFourGame()
        solveFourByFourGame()

        pressBack()
        composeRule.waitForIdle()

        composeRule
            .onAllNodesWithTag(GAME_VICTORY_DIALOG_TEST_TAG)
            .assertCountEquals(0)
        composeRule
            .onNodeWithTag(GAME_CHESS_BOARD_TEST_TAG)
            .assertIsDisplayed()
        composeRule
            .onNodeWithTag(GAME_PROGRESS_TEST_TAG)
            .assertTextEquals(queensLeftLabel(queensLeft = 0))
    }

    @Test
    fun compactHeightVictoryKeepsHeroAboveTitleAndActionsVisible() {
        setOrientation(
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
            expectedOrientation = Configuration.ORIENTATION_LANDSCAPE,
        )
        startFourByFourGame()
        solveFourByFourGame()

        val heroBounds = composeRule
            .onNodeWithTag(GAME_VICTORY_HERO_TEST_TAG)
            .getUnclippedBoundsInRoot()
        val titleBounds = composeRule
            .onNodeWithTag(GAME_VICTORY_TITLE_TEST_TAG)
            .getUnclippedBoundsInRoot()

        assertTrue(
            "The victory hero should not overlap the title.",
            heroBounds.bottom <= titleBounds.top,
        )
        composeRule
            .onNodeWithTag(GAME_VICTORY_PLAY_AGAIN_TEST_TAG)
            .assertIsDisplayed()
        composeRule
            .onNodeWithTag(GAME_VICTORY_CHOOSE_SIZE_TEST_TAG)
            .assertIsDisplayed()
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

    private fun startFourByFourGame() {
        selectBoardSize(size = 4)
        startGame()
    }

    private fun solveFourByFourGame() {
        clickCell(row = 0, column = 1)
        clickCell(row = 1, column = 3)
        clickCell(row = 2, column = 0)
        clickCell(row = 3, column = 2)
    }

    private fun setOrientation(
        requestedOrientation: Int,
        expectedOrientation: Int,
    ) {
        composeRule.activity.requestedOrientation = requestedOrientation
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.activity.resources.configuration.orientation == expectedOrientation
        }
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

    private fun assertStatusIsAboveBoardAndProgressIsBelowBoard() {
        val statusBounds = composeRule
            .onNodeWithTag(GAME_STATUS_TEST_TAG)
            .getUnclippedBoundsInRoot()
        val boardBounds = composeRule
            .onNodeWithTag(GAME_CHESS_BOARD_TEST_TAG)
            .getUnclippedBoundsInRoot()
        val progressBounds = composeRule
            .onNodeWithTag(GAME_PROGRESS_TEST_TAG)
            .getUnclippedBoundsInRoot()

        assertTrue(
            "Game status should be above the board.",
            statusBounds.bottom <= boardBounds.top,
        )
        assertTrue(
            "Game progress should be below the board.",
            progressBounds.top >= boardBounds.bottom,
        )
    }

    private fun gameInstruction(boardSize: Int): String =
        composeRule.activity.getString(R.string.game_instruction, boardSize)

    private fun queensLeftLabel(queensLeft: Int): String =
        composeRule.activity.resources.getQuantityString(
            R.plurals.game_queens_left,
            queensLeft,
            queensLeft,
        )

    private fun gameSolvedMessage(boardSize: Int): String =
        composeRule.activity.getString(R.string.game_status_solved_message, boardSize)

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
