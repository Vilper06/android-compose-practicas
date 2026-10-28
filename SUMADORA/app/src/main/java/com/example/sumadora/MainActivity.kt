package com.example.sumadora

import android.os.Bundle
import android.content.Context
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainActivityUI()
        }
    }
}

fun getAndroidId(contexto: Context): String {
    return Settings.Secure.getString(
        contexto.contentResolver,
        Settings.Secure.ANDROID_ID
    ) ?: "desconocido"
}

@Composable
fun MainActivityUI() {
    val contexto = LocalContext.current
    var operando1 by remember { mutableStateOf("") }
    var operando2 by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        Arrangement.Top,
        Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Operando 1")
            Text("Operando 2")
            Text("Resultado")
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextField(
                value = operando1,
                onValueChange = { operando1 = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
            Text("+")
            TextField(
                value = operando2,
                onValueChange = { operando2 = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
            Text("=")
            TextField(
                value = resultado,
                onValueChange = { resultado = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                operando1 = ""
                operando2 = ""
                resultado = ""
            }) {
                Text("Limpiar")
            }
            Button(onClick = {
                val num1 = operando1.toIntOrNull() ?: 0
                val num2 = operando2.toIntOrNull() ?: 0
                resultado = (num1 + num2).toString()
            }) {
                Text("Calcular")
            }
        }

        Text(
            text = getAndroidId(contexto),
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}