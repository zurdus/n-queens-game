package com.zurdus.nqueens.feature.boardsize.di

import com.zurdus.nqueens.feature.boardsize.presentation.BoardSizeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val boardSizeModule = module {
    viewModelOf(::BoardSizeViewModel)
}
