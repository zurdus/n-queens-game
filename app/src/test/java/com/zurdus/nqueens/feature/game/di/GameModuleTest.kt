package com.zurdus.nqueens.feature.game.di

import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify

@OptIn(KoinExperimentalAPI::class)
class GameModuleTest {

    @Test
    fun `game module provides all ViewModel dependencies`() {
        gameModule.verify(
            extraTypes = listOf(Int::class),
        )
    }
}
