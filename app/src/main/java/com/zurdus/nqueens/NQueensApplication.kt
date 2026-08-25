package com.zurdus.nqueens

import android.app.Application
import com.zurdus.nqueens.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NQueensApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@NQueensApplication)
            modules(appModules)
        }
    }
}
