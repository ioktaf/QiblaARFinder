package com.qiblaarfinder

import android.app.Application
import com.qiblaarfinder.di.AppContainer

class QiblaFinderApplication : Application() {
    val appContainer: AppContainer by lazy {
        AppContainer(applicationContext)
    }
}

