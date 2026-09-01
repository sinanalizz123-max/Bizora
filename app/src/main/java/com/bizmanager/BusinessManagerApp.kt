package com.bizmanager

import android.app.Application
import com.bizmanager.data.AppContainer

class BusinessManagerApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
