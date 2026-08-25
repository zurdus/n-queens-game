package com.zurdus.nqueens.feature.boardsize.presentation

import androidx.window.core.layout.WindowSizeClass
import org.junit.Assert.assertEquals
import org.junit.Test

class BoardSizeScreenTest {

    @Test
    fun `compact width uses compact vertical layout`() {
        assertEquals(
            BoardSizeLayout.VERTICAL_COMPACT,
            windowSizeClass(widthDp = 599, heightDp = 360).toBoardSizeLayout(),
        )
        assertEquals(
            BoardSizeLayout.VERTICAL_COMPACT,
            windowSizeClass(widthDp = 599, heightDp = 800).toBoardSizeLayout(),
        )
    }

    @Test
    fun `medium width and medium height use medium vertical layout`() {
        assertEquals(
            BoardSizeLayout.VERTICAL_MEDIUM,
            windowSizeClass(widthDp = 600, heightDp = 480).toBoardSizeLayout(),
        )
        assertEquals(
            BoardSizeLayout.VERTICAL_MEDIUM,
            windowSizeClass(widthDp = 839, heightDp = 960).toBoardSizeLayout(),
        )
    }

    @Test
    fun `medium width and compact height use horizontal layout`() {
        assertEquals(
            BoardSizeLayout.HORIZONTAL,
            windowSizeClass(widthDp = 600, heightDp = 479).toBoardSizeLayout(),
        )
        assertEquals(
            BoardSizeLayout.HORIZONTAL,
            windowSizeClass(widthDp = 800, heightDp = 360).toBoardSizeLayout(),
        )
    }

    @Test
    fun `expanded and larger widths use horizontal layout`() {
        assertEquals(
            BoardSizeLayout.HORIZONTAL,
            windowSizeClass(widthDp = 840, heightDp = 360).toBoardSizeLayout(),
        )
        assertEquals(
            BoardSizeLayout.HORIZONTAL,
            windowSizeClass(widthDp = 1_600, heightDp = 800).toBoardSizeLayout(),
        )
    }

    private fun windowSizeClass(
        widthDp: Int,
        heightDp: Int,
    ): WindowSizeClass =
        WindowSizeClass(
            minWidthDp = widthDp,
            minHeightDp = heightDp,
        )
}
