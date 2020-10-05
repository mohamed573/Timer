package com.app.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        TODO("TimerExpiredReceiver.onReceive() is not implemented")
        // TODO : show notification

        PrefUtil.setTimerState(TimerActivity.TimerState.Stopped, context)
        PrefUtil.setAlarmTime(0 , context)
    }
}
