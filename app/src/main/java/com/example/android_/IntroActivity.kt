package com.example.android_

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONObject

private fun getData(stURL: String) : String?{

    val client = OkHttpClient()
    val request = okhttp3.Request.Builder()
        .url(stURL)
        .build()
    return try {
        client.newCall(request).execute().use { response: Response ->
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


class IntroActivity : ComponentActivity() {

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

    private fun executerAsynchrone( ip: String, port : String){
        var thread = Thread {
            Log.i("thread", "tester le log")


            /*10.4.129.44:1236*/
            val reponse = getData("http://" + ip + ":" + port + "/")


            if(reponse != null){
                Log.i("Mon_get", reponse)
            }else
                Toast.makeText(this, "L'ip ou le port n'est pas valide", Toast.LENGTH_SHORT).show()
            }

        thread.start()
        }





    private lateinit var btnsenddata: Button
    private lateinit var input_port: EditText
    private lateinit var input_ip: EditText




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_activity)
        enableEdgeToEdge()

        btnsenddata = findViewById(R.id.button_enter)

        btnsenddata.setOnClickListener {
            input_ip = findViewById(R.id.input_ip)
            input_port = findViewById(R.id.input_port)
            val newIp = input_ip.text.toString()
            val newPort = input_port.text.toString()
            Log.i("tag_ip", input_ip.text.toString())
            Log.i("tag_port", input_port.text.toString())



            try {

                executerAsynchrone(input_ip.text.toString(),input_port.text.toString())


            }catch (e:Exception){
                Toast.makeText(this, "L'ip ou le port n'est pas valide", Toast.LENGTH_SHORT).show()
            }
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("ip", input_ip.text.toString())
            intent.putExtra("port", input_port.text.toString())
            startActivity(intent)







        }






    }
}

