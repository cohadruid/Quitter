package hr.ferit.dariocoric.quitter

import android.app.Application
import android.content.Context

class Quitter: Application() {
    companion object {
        lateinit var ApplicationContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        ApplicationContext = this
    }
}