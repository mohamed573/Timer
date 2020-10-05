package com.app.timer

import android.content.Context
import androidx.preference.PreferenceManager


class PrefUtil {


    // static member
       companion object{
        private const val TIMER_LENGTH_ID = "timer.app.com.timer_length"
           fun getTimerLength(context : Context): Int{
               val preference = PreferenceManager.getDefaultSharedPreferences(context)
               return preference.getInt(TIMER_LENGTH_ID , 10)
           }

        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "timer.app.com.util"

        fun getPreviousTimerLengthSeconds(context: Context) : Long{
           val preference = PreferenceManager.getDefaultSharedPreferences(context)
               return  preference.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID , 0)
        }
        fun setPreviousTimerLengthSeconds(seconds : Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID , seconds)
            editor.apply()
        }

        private const val TIMER_STATE_ID = "timer.app.com.util.min_remaining"

        fun getTimerState(context: Context): TimerActivity.TimerState{
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal =preference.getInt(TIMER_STATE_ID , 0)
            return TimerActivity.TimerState.values()[ordinal]
        }

        fun setTimerState(state : TimerActivity.TimerState, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID , ordinal)
            editor.apply()
        }

        private const val SECONDS_REMAINING_ID = "timer.app.com.util.seconds"

        fun getSecondsRemaining(context: Context) : Long{
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            return  preference.getLong( SECONDS_REMAINING_ID, 0)
        }
        fun setSecondsRemaining(seconds : Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID , seconds)
            editor.apply()
        }

        private const val ALARM_SET_TIME_ID = "timer.app.com.util.backgrounded_time"

        fun getAlarmSetTime(context : Context) :Long{
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            return preference.getLong(ALARM_SET_TIME_ID , 0)
        }

        fun setAlarmTime(time : Long , context : Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID , time)
            editor.apply()
        }

       }

}