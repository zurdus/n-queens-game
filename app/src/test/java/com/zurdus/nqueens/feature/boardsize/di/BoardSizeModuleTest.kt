package com.zurdus.nqueens.feature.boardsize.di

import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify

@OptIn(KoinExperimentalAPI::class)
class BoardSizeModuleTest {

    @Test
    fun `board-size module provides all ViewModel dependencies`() {
        boardSizeModule.verify()
    }
}
