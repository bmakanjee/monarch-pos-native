package com.monarch.pos

import android.app.Application
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.TerminalListener
import com.stripe.stripeterminal.external.models.ConnectionStatus
import com.stripe.stripeterminal.external.models.PaymentStatus
import com.stripe.stripeterminal.log.LogLevel

class MonarchApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Terminal.initTerminalForAOD(
            context = this,
            logLevel = LogLevel.VERBOSE,
            listener = object : TerminalListener {
                override fun onUnexpectedReaderDisconnect(reader: com.stripe.stripeterminal.external.models.Reader) {}
                override fun onConnectionStatusChange(status: ConnectionStatus) {}
                override fun onPaymentStatusChange(status: PaymentStatus) {}
            }
        )
    }
}
