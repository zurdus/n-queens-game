package com.zurdus.nqueens.feature.boardsize.presentation

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zurdus.nqueens.MainActivity
import com.zurdus.nqueens.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BoardSizeScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun usePortraitOrientation() {
        setOrientation(
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
            expectedOrientation = Configuration.ORIENTATION_PORTRAIT,
        )
    }

    @Test
    fun defaultSelectionRendersInteractiveEightByEightBoard() {
        val activity = composeRule.activity

        composeRule
            .onNodeWithText(activity.getString(R.string.board_size_title))
            .assertIsDisplayed()
        composeRule
            .onNodeWithTag(BOARD_SIZE_SELECTED_VALUE_TEST_TAG)
            .assertTextEquals(boardSizeLabel(8))
        composeRule
            .onNodeWithTag(BOARD_SIZE_SLIDER_TEST_TAG)
            .assertRangeInfoEquals(
                ProgressBarRangeInfo(
                    current = 8f,
                    range = 4f..12f,
                    steps = 7,
                ),
            )
            .assertContentDescriptionEquals(
                activity.getString(R.string.board_size_slider_content_description),
            )
        composeRule
            .onNodeWithTag(BOARD_SIZE_CHESS_BOARD_TEST_TAG)
            .assertContentDescriptionEquals(boardDescription(8))
        composeRule
            .onNodeWithTag(BOARD_SIZE_START_GAME_TEST_TAG)
            .assertIsDisplayed()

        assertBoardCellCount(64)
        assertBoardIsSquare()
        assertSelectedValueIsBelowBoard()
        assertStartGameIsPinnedToBottom()

        val sliderBounds = composeRule
            .onNodeWithTag(BOARD_SIZE_SLIDER_TEST_TAG)
            .getUnclippedBoundsInRoot()
        val boardBounds = composeRule
            .onNodeWithTag(BOARD_SIZE_CHESS_BOARD_TEST_TAG)
            .getUnclippedBoundsInRoot()

        assertTrue(
            "Vertical layout should place the board below the controls.",
            boardBounds.top >= sliderBounds.bottom,
        )
    }

    @Test
    fun minimumSelectionUpdatesBoardImmediately() {
        selectBoardSize(4)

        composeRule
            .onNodeWithTag(BOARD_SIZE_SELECTED_VALUE_TEST_TAG)
            .assertTextEquals(boardSizeLabel(4))
        composeRule
            .onNodeWithTag(BOARD_SIZE_CHESS_BOARD_TEST_TAG)
            .assertContentDescriptionEquals(boardDescription(4))
        assertBoardCellCount(16)
    }

    @Test
    fun maximumSelectionUpdatesBoardImmediately() {
        selectBoardSize(12)

        composeRule
            .onNodeWithTag(BOARD_SIZE_SELECTED_VALUE_TEST_TAG)
            .assertTextEquals(boardSizeLabel(12))
        composeRule
            .onNodeWithTag(BOARD_SIZE_CHESS_BOARD_TEST_TAG)
            .assertContentDescriptionEquals(boardDescription(12))
        assertBoardCellCount(144)
    }

    @Test
    fun landscapePlacesBoardBesideControls() {
        setOrientation(
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
            expectedOrientation = Configuration.ORIENTATION_LANDSCAPE,
        )

        val controlsBounds = composeRule
            .onNodeWithTag(BOARD_SIZE_CONTROLS_TEST_TAG)
            .assertIsDisplayed()
            .getUnclippedBoundsInRoot()
        val boardBounds = composeRule
            .onNodeWithTag(BOARD_SIZE_CHESS_BOARD_TEST_TAG)
            .assertIsDisplayed()
            .getUnclippedBoundsInRoot()

        assertTrue(
            "Horizontal layout should place the board beside the controls.",
            boardBounds.left >= controlsBounds.right,
        )
        val controlsWidth = (controlsBounds.right - controlsBounds.left).value
        assertTrue(
            "Controls should respect their maximum width. Actual width: $controlsWidth dp.",
            controlsWidth <= 360.5f,
        )
        assertBoardIsSquare()
        assertSelectedValueIsBelowBoard()
        assertStartGameIsPinnedToBottom()
        assertBoardGroupIsNearBottomAction()
    }

    private fun selectBoardSize(size: Int) {
        composeRule
            .onNodeWithTag(BOARD_SIZE_SLIDER_TEST_TAG)
            .performSemanticsAction(SemanticsActions.SetProgress) { setProgress ->
                setProgress(size.toFloat())
            }
        composeRule.waitForIdle()
    }

    private fun assertBoardCellCount(expectedCount: Int) {
        composeRule
            .onAllNodes(BOARD_CELL_MATCHER, useUnmergedTree = true)
            .assertCountEquals(expectedCount)
    }

    private fun assertBoardIsSquare() {
        val boardBounds = composeRule
            .onNodeWithTag(BOARD_SIZE_CHESS_BOARD_TEST_TAG)
            .getUnclippedBoundsInRoot()

        assertEquals(
            "Board width and height should match.",
            (boardBounds.right - boardBounds.left).value,
            (boardBounds.bottom - boardBounds.top).value,
            0.5f,
        )
    }

    private fun assertSelectedValueIsBelowBoard() {
        val boardBounds = composeRule
            .onNodeWithTag(BOARD_SIZE_CHESS_BOARD_TEST_TAG)
            .getUnclippedBoundsInRoot()
        val selectedValueBounds = composeRule
            .onNodeWithTag(BOARD_SIZE_SELECTED_VALUE_TEST_TAG)
            .getUnclippedBoundsInRoot()

        assertTrue(
            "Selected value should be below the board.",
            selectedValueBounds.top >= boardBounds.bottom,
        )
    }

    private fun assertStartGameIsPinnedToBottom() {
        val selectedValueBounds = composeRule
            .onNodeWithTag(BOARD_SIZE_SELECTED_VALUE_TEST_TAG)
            .getUnclippedBoundsInRoot()
        val buttonBounds = composeRule
            .onNodeWithTag(BOARD_SIZE_START_GAME_TEST_TAG)
            .getUnclippedBoundsInRoot()
        val rootBounds = composeRule
            .onRoot()
            .getUnclippedBoundsInRoot()

        assertTrue(
            "Start Game should be below the selector content.",
            buttonBounds.top >= selectedValueBounds.bottom,
        )
        assertTrue(
            "Start Game should stay inside the bottom safe bounds.",
            buttonBounds.bottom <= rootBounds.bottom,
        )
        assertTrue(
            "Start Game should respect its maximum width.",
            (buttonBounds.right - buttonBounds.left).value <= 360.5f,
        )
    }

    private fun assertBoardGroupIsNearBottomAction() {
        val selectedValueBounds = composeRule
            .onNodeWithTag(BOARD_SIZE_SELECTED_VALUE_TEST_TAG)
            .getUnclippedBoundsInRoot()
        val buttonBounds = composeRule
            .onNodeWithTag(BOARD_SIZE_START_GAME_TEST_TAG)
            .getUnclippedBoundsInRoot()

        val boardToButtonGap = (buttonBounds.top - selectedValueBounds.bottom).value
        assertTrue(
            "Horizontal board content should sit near the bottom action. " +
                "Actual gap: $boardToButtonGap dp.",
            boardToButtonGap <= 56.5f,
        )
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

    private fun boardSizeLabel(size: Int): String =
        composeRule.activity.getString(R.string.board_size_dimension, size, size)

    private fun boardDescription(size: Int): String =
        composeRule.activity.getString(
            R.string.board_preview_content_description,
            size,
            size,
        )

    private companion object {
        val BOARD_CELL_MATCHER = SemanticsMatcher("Board cell test tag") { node ->
            SemanticsProperties.TestTag in node.config &&
                node.config[SemanticsProperties.TestTag].startsWith("chess_board_cell_")
        }
    }
}
