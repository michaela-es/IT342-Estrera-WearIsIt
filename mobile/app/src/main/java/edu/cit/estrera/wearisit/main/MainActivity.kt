package edu.cit.estrera.wearisit.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import edu.cit.estrera.wearisit.auth.ui.AuthActivity
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this, AuthActivity::class.java))
    }
}