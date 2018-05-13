package pro.yagupov.stepcounterwithwidget.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

import pro.yagupov.stepcounterwithwidget.BuildConfig


/**
 * Created by developer on 11.06.17.
 */

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (BuildConfig.DEBUG) {
            Toast.makeText(context, "BootReceiver was starting - Only debug", Toast.LENGTH_LONG).show()
        }

        context.startService(Intent(context, StepCounterService::class.java))
    }
}
