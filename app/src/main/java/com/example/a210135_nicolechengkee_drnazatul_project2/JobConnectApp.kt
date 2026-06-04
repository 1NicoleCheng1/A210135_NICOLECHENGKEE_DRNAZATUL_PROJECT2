package com.example.a210135_nicolechengkee_drnazatul_project2

import android.app.Application
import com.google.firebase.FirebaseApp


class JobConnectApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
