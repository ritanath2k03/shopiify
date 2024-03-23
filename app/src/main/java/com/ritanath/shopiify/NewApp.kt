package com.ritanath.shopiify

import android.app.Application
import android.util.Log
import android.widget.Toast
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NewApp: Application(){
    override fun onCreate() {
        super.onCreate()
        Log.d("main","working")
    }
}