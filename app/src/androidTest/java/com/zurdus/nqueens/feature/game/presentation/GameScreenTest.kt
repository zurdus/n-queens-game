package com.zurdus.nqueens.feature.game.presentation

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.ui.semantics.SemanticsActions
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
        assertGameBoardPlacement()
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
    fun tappingCellsPlacesQueensAndMarksConflicts() {
        startGame()

        clickCell(row = 0, column = 0)
        composeRule
            .onNodeWithTag("chess_board_cell_0_0", useUnmergedTree = true)
            .assertContentDescriptionEquals(
                cellDescription(R.string.game_cell_queen_content_description, 1, 1),
            )

        clickCell(row = 0, column = 2)
        composeRule
            .onNodeWithTag("chess_board_cell_0_0", useUnmergedTree = true)
            .assertContentDescriptionEquals(
                cellDescription(R.string.game_cell_conflict_content_description, 1, 1),
            )
        composeRule
            .onNodeWithTag("chess_board_cell_0_2", useUnmergedTree = true)
            .assertContentDescriptionEquals(
                cellDescription(R.string.game_cell_conflict_content_description, 1, 3),
            )
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

    private fun clickCell(row: Int, column: Int) {
        composeRule
            .onNodeWithTag("chess_board_cell_${row}_$column", useUnmergedTree = true)
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

    private fun assertGameBoardPlacement() {
        val contentBounds = composeRule
            .onNodeWithTag(GAME_CONTENT_TEST_TAG)
            .getUnclippedBoundsInRoot()
        val instructionBounds = composeRule
            .onNodeWithTag(GAME_INSTRUCTION_TEST_TAG)
            .getUnclippedBoundsInRoot()
        val boardBounds = composeRule
            .onNodeWithTag(GAME_CHESS_BOARD_TEST_TAG)
            .getUnclippedBoundsInRoot()

        assertEquals(
            "Instruction should be 16dp above the board.",
            16f,
            (boardBounds.top - instructionBounds.bottom).value,
            0.5f,
        )
        assertEquals(
            "Game board should be horizontally centered.",
            (contentBounds.left.value + contentBounds.right.value) / 2,
            (boardBounds.left.value + boardBounds.right.value) / 2,
            0.5f,
        )
        assertEquals(
            "Game board should be vertically centered.",
            (contentBounds.top.value + contentBounds.bottom.value) / 2,
            (boardBounds.top.value + boardBounds.bottom.value) / 2,
            0.5f,
        )
    }

    private fun gameInstruction(boardSize: Int): String =
        composeRule.activity.getString(R.string.game_instruction, boardSize)

    private fun gameBoardDescription(boardSize: Int): String =
        composeRule.activity.getString(
            R.string.game_board_content_description,
            boardSize,
            boardSize,
        )

    private fun boardSizeLabel(size: Int): String =
        composeRule.activity.getString(R.string.board_size_dimension, size, size)

    private fun cellDescription(resourceId: Int, row: Int, column: Int): String =
        composeRule.activity.getString(resourceId, row, column)

    private companion object {
        val CHESS_BOARD_CELL_MATCHER = SemanticsMatcher("Chessboard cell test tag") { node ->
            SemanticsProperties.TestTag in node.config &&
                node.config[SemanticsProperties.TestTag].startsWith("chess_board_cell_")
        }
    }
}
