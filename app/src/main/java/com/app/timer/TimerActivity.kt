package com.app.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.content_timer.*
import timer.app.com.R
import java.util.*

// The companion object is a singleton, and its members can be accessed directly via the name of the containing class

private const val TAG = "TimerActivity"

class TimerActivity : AppCompatActivity() {
    enum class TimerState{
        Stopped , Paused , Running
    }

    companion object{
        @RequiresApi(Build.VERSION_CODES.KITKAT)
        fun setAlarm(context: Context, nowSeconds : Long, secondsRemaining : Long):Long{
            val wakUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context , TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context , 0 , intent , 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP , wakUpTime , pendingIntent)
            PrefUtil.setAlarmTime(nowSeconds , context)
            return wakUpTime
        }

        fun removeAlarm(context: Context){
            val intent = Intent(context , TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context , 0 , intent , 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmTime(0 , context)
        }

        val nowSeconds : Long
        get() = Calendar.getInstance().timeInMillis / 1000
    }

    private lateinit var timer : CountDownTimer
    private var timerLengthSeconds = 0L
    private var timerState = TimerState.Stopped

    private var  secondsRemaining = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setIcon(R.drawable.ic_timer)
        supportActionBar?.title = "          Timer"

        fab_start.setOnClickListener { v->
            startTimer()
            timerState = TimerState.Running
            updateButton()
        }
        fab_pause.setOnClickListener { v->
            timer.cancel()
            timerState  = TimerState.Paused
            updateButton()
        }

        fab_stop.setOnClickListener { v->
            timer.cancel()
            onTimerFinished()
        }
    }

    override fun onResume() {
        Log.d(TAG , "onResume : Called")
        super.onResume()
        initTimer()

        removeAlarm(this)

        removeAlarm(this)
        NotificationUtil.hideTimerNotification(this)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onPause() {
        Log.d(TAG , "onPause : Called")
        super.onPause()

        if(timerState == TimerState.Running){
            timer.cancel()
            val wakeUpTime = setAlarm(this , nowSeconds , secondsRemaining)
            NotificationUtil.showTimerRunning(this , wakeUpTime)
        }else if(timerState == TimerState.Paused){
            NotificationUtil.showTimerPaused(this)
        }

        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds , this)
        PrefUtil.setSecondsRemaining(secondsRemaining , this)
        PrefUtil.setTimerState(timerState , this)



    }

    private fun initTimer(){

        //we don't want to change the length of the timer which is already running
        //if the length was changed in settings while it was backgrounded

        timerState = PrefUtil.getTimerState(this)

        if(timerState == TimerState.Stopped)
            setNewTimerLength()
        else
            setPreviousTimerLength()

        secondsRemaining = if(timerState == TimerState.Running || timerState == TimerState.Paused)
            PrefUtil.getSecondsRemaining(this)
        else
            timerLengthSeconds

        val alarmSetTime = PrefUtil.getAlarmSetTime(this)
        if(alarmSetTime > 0)
            secondsRemaining -= nowSeconds - alarmSetTime

        if(secondsRemaining <= 0)
            onTimerFinished()

        //resume where we left off
       else if(timerState == TimerState.Running)
            startTimer()

        updateButton()
        updateCountdownUI()
    }

    private fun onTimerFinished(){

        timerState = TimerState.Stopped

        //set the length of the timer to be the one set in SettingsActivity
        //if the length was changed when the timer was running

        setNewTimerLength()
        progress_countdown.progress = 0

        PrefUtil.setSecondsRemaining(timerLengthSeconds , this)
        secondsRemaining = timerLengthSeconds

        updateButton()
        updateCountdownUI()

    }
    private fun startTimer(){
        timerState = TimerState.Running

        timer = object: CountDownTimer(secondsRemaining * 1000 , 1000){
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }

    private fun setNewTimerLength(){
        val lengthInMinutes = PrefUtil.getTimerLength(this)
        timerLengthSeconds = (lengthInMinutes * 60L)
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun setPreviousTimerLength(){
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(this)
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun updateCountdownUI(){

        val minutesUtilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUtilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        textView_countdown.text =  "$minutesUtilFinished:${if (secondsStr.length == 2) secondsStr else "0" + secondsStr}"
        progress_countdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

    private fun updateButton(){
        when (timerState) {
            TimerState.Running ->{
                fab_start.isEnabled = false
                fab_pause.isEnabled = true
                fab_stop.isEnabled = true
            }
            TimerState.Stopped -> {
                fab_start.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = false
            }
            TimerState.Paused -> {
                fab_start.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = true
            }
        }
    }





    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this , SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}