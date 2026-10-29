package com.sportmaps.app

import android.app.Application
import com.sportmaps.app.data.AppContainer

/** Класс приложения: создаёт единый контейнер зависимостей. */
class SportMapsApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
