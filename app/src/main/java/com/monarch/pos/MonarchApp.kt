package com.monarch.pos

import android.app.Application
import com.stripe.stripeterminal.TerminalApplicationDelegate

class MonarchApp : Application() {
    override fun onCreate() {
        super.onCreate()
        TerminalApplicationDelegate.onCreate(this)
    }
}
