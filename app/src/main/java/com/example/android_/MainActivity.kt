package com.example.android_

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.icu.text.DateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.PixelCopy.Request
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.android_.ui.theme.Android_Theme
import java.util.concurrent.TimeUnit
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar


val handler = Handler(Looper.getMainLooper())
class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams){

    override fun doWork(): Result {
        Log.i("Mon_Worker", "Affichage d'un log en arriere plan")
        return Result.success()
    }

}
//fonction pour récuperer les données recues en se connectant à une addresse ip et un port
private fun getData(stURL: String) : String?{

    val client = OkHttpClient()
    val request = okhttp3.Request.Builder()
        .url(stURL)
        .build()
    return try {
        client.newCall(request).execute().use { response:Response ->
            if(!response.isSuccessful){
                Log.e("ERREUR", "Erreur de connexion :  ${response.code} ")
                null
            }else{
                response.body?.string()
            }
        }
    }catch (e:Exception){
        e.printStackTrace()
        Log.e("Erreur", e.toString())
        null
    }

}



class MainActivity : ComponentActivity() {
    //valeur par défaut
    private lateinit var txtTemperature : TextView
    private lateinit var btnActualiser : Button
    private lateinit var btnModifier: Button
    private lateinit var btnparametre : Button






    /*
    private fun demanderPermissionNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->

                if(!result){
                    Toast.makeText(this,"La permission n'a pas été accordée",Toast.LENGTH_SHORT).show()
                }
            }
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS) != PermissionChecker.PERMISSION_GRANTED)
            {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

        }
    }
*/



    //fonction pour se connecter et recevoir les données du serveur
    private fun executerAsynchrone(){
        var thread = Thread {
            Log.i("thread", "afficher log asynchrone")


            /*10.4.129.44:1236*/
            val adresse_ip = intent.getStringExtra("ip")
            val port = intent.getStringExtra("port")
            Log.i("recu",adresse_ip.toString())

            val reponse = getData("http://" + adresse_ip+ ":" + port + "/")


            if(reponse != null){
                Log.i("Mon_get", reponse)
            }else
            {
                Log.i("Mon_get", "null")
            }


            handler.post{
                try
                {
                    Log.d("JSON",reponse ?: "Données nulles")

        //Récuperer les données json et les afficher sur les textview
                    if(reponse != null){
                        val obj = JSONObject(reponse)
                        val mouvement = obj.getString("mouvement")
                        val temperature = obj.getString("temperature")
                        val frequence = obj.getString("frequence")
                        val limite = obj.getString("limite")
                        val txtMov = findViewById<TextView>(R.id.txtMov)
                        val txt_temp = findViewById<TextView>(R.id.txt_temp)
                        val txt_freq = findViewById<TextView>(R.id.txt_frequence)
                        val txt_limite = findViewById<TextView>(R.id.txt_limit)
                        //j'assigne les données au textview pour les afficher
                        txtMov.text = mouvement.toString()
                        txt_temp.text = temperature.toString()
                        txt_freq.text = frequence.toString()
                        txt_limite.text = limite.toString()



                        //Log.i("mon_state", state.toString())
                        Log.i("mon_temp", temperature.toString())
                        Log.i("mon_freq", frequence.toString())
                        }

//,{'temperature':temp}
                }catch (e: Exception){
                    e.printStackTrace()
                    Log.e("ERREUR",e.toString())
                }
            }
        }

        thread.start()
    }


//JSON





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        //val adresse_ip = intent.getStringExtra("ip")

        //Log.i("new_ip",adresse_ip.toString())

        executerAsynchrone()
        //j'affiche le temps d'aujourd'hui
       val calendar = Calendar.getInstance().time
       val dateFormat = DateFormat.getDateInstance(DateFormat.FULL).format(calendar)
       val timeFormat = DateFormat.getTimeInstance().format(calendar)

       val datetxtview = findViewById<TextView>(R.id.txt_Date)
       datetxtview.text = dateFormat

/*
        val thread = Thread{
            sendPost("http://192.168.2.38:1236/", "{\"temp\":38}")
         }
        thread.start()

 */
        //j'assigne mes boutons
        btnActualiser = findViewById(R.id.button_actualiser)
        //permet d'actualiser les données recu en json
        btnActualiser.setOnClickListener {
            executerAsynchrone()
        }

        btnModifier = findViewById(R.id.btn_modifier)
        //changer de page avec les bouton modifier et paramètre

         val ip_recu = intent.getStringExtra("ip")
         val port = intent.getStringExtra("port")

        //bouton modifier pour envoyer l'ip le port qui va servir à envoyer les données au serveur
        btnModifier.setOnClickListener {
            val intent = Intent(this,ModifierActivity::class.java)
            intent.putExtra("ip",ip_recu.toString())
            intent.putExtra("port", port.toString())
            val txt_limite = findViewById<TextView>(R.id.txt_limit)
            intent.putExtra("limite",txt_limite.text.toString() )

            startActivity(intent)

        }
        btnparametre = findViewById(R.id.btn_parametre)
        btnparametre.setOnClickListener {
            val intent = Intent(this,ParametreActivity::class.java)
            startActivity(intent)

        }


        //un worker qui se refresh toute les 15 minutes
        val requete = PeriodicWorkRequest.Builder(
            MyWorker::class.java,
            15, TimeUnit.MINUTES

        ).build()

        WorkManager.getInstance(this).enqueue(requete)


    }
}




