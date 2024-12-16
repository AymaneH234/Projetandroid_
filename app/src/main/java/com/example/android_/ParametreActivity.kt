package com.example.android_

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.example.android_.ui.theme.Android_Theme

class ParametreActivity : ComponentActivity() {
    val CHANNEL_ID = "Channel1"
    private fun demanderPermissionNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->

                if(!result){
                    Toast.makeText(this,"La permission n'a pas été accordée", Toast.LENGTH_SHORT).show()
                }
            }
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PermissionChecker.PERMISSION_GRANTED)
            {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

        }
    }


//fonction pour creer le canal
    private fun creerCanal(){
        val channelName = "Channel1"
        val channelDescription = "Canal de notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val channel = NotificationChannel(CHANNEL_ID,channelName,importance).apply {
                description = channelDescription
            }
            val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var btnretour : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parametre)
        enableEdgeToEdge()

        val notificationCheckBox = findViewById<CheckBox>(R.id.notificationCheckBox)


        // Initialiser les préférences partagées
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)

        // Charger l'état de la CheckBox depuis les préférences
        val notificationsEnabled = sharedPreferences.getBoolean("notificationsEnabled", false)
        notificationCheckBox.isChecked = notificationsEnabled

        // Enregistrer l'état de la CheckBox lorsque l'utilisateur change son état
        notificationCheckBox.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("notificationsEnabled", isChecked).apply()
        }
        if(notificationCheckBox.isChecked)
        {
            demanderPermissionNotification()
            creerCanal()
        }


        btnretour = findViewById(R.id.btn_retour_page_parametre)
        btnretour.setOnClickListener {
            finish()
            
        }

    }
}

