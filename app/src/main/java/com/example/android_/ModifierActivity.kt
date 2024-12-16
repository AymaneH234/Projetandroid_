package com.example.android_

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ModifierActivity : ComponentActivity() {

    val CHANNEL_ID = "Channel1"
    //private lateinit var sharedPreferences: SharedPreferences




//fonction pour afficher la notification_
    private fun afficherNotification(id: Int, titre: String, texte:String){
        val builder = NotificationCompat.Builder(applicationContext,CHANNEL_ID)
            .setSmallIcon(android.R.drawable.star_on)
            .setContentTitle(titre)
            .setContentText(texte)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        //afficher la notification
        with(NotificationManagerCompat.from(applicationContext)){
            if(ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            )== PackageManager.PERMISSION_GRANTED
           ){
                notify(id,builder.build())
            }
        }




    }















    //fonction pour envoyer des données au serveur
    private fun sendPost(stURL: String, jsonMsg: String){
        try {

            val url = URL(stURL)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            conn.doInput = true
            DataOutputStream(conn.outputStream).use { os ->
                os.writeBytes(jsonMsg)
                os.flush()
            }
            Log.d("STATUS", conn.responseCode.toString())
            Log.d("MSG", conn.responseMessage)
            conn.disconnect()



        }catch (e:Exception){
            e.printStackTrace()
            Log.e("ERREUR", e.message ?: "Erreur inconnue")
        }



    }

//assigner les bouton
    private lateinit var btnretour : Button
    private lateinit var btnconfirmer : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifier)
        enableEdgeToEdge()
        //Log.i("nouvelle", intent.getStringExtra("ip").toString())


        btnconfirmer = findViewById(R.id.btn_confirmer)
        btnconfirmer.setOnClickListener {


            val input_limite = findViewById<EditText>(R.id.editText_limit)

            val ip =  intent.getStringExtra("ip").toString()
            val port = intent.getStringExtra("port").toString()
            val limite = intent.getStringExtra("limite").toString()
            Log.i("limite",input_limite.text.toString())

            val isNumeric = input_limite.text.toString().toIntOrNull() != null
            if(input_limite.text.toString().isEmpty() || !isNumeric)
            {
                Toast.makeText(this, "Le champ est incorrect, Veuillez réessayé", Toast.LENGTH_SHORT).show()
            }else
            {
                //commencer le thread avec l'ip et le port recu et envoyer la nouvelle limite sur le serveur avec la commande 'lim'
                val thread = Thread{
                    sendPost("http://" + ip+ ":"  + port + "/", "{\"lim\":" + input_limite.text.toString() + "}")
                }
                thread.start()
                Toast.makeText(this, "la limite à été changé pour : " + input_limite.text.toString() + " degrés", Toast.LENGTH_SHORT).show()
                //val limite = intent.getStringExtra("limite").toString()
                val titre = "Nouvelle limite"
                val texte = "La limite de " + limite + " à été changé pour " + input_limite.text.toString() + " degré"
                val notificationID = 1
                afficherNotification(notificationID,titre,texte)
            }
         }


        /*
                val thread = Thread{
                    sendPost("http://192.168.2.38:1236/", "{\"temp\":38}")
                 }
                thread.start()

         */

//btn retour
        btnretour = findViewById(R.id.btn_retour_page_modifier)
        btnretour.setOnClickListener {
            finish()

        }

        

    }
}

