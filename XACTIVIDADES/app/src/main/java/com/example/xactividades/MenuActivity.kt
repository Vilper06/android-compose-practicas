package com.example.xactividades

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.xactividades.ui.theme.XACTIVIDADESTheme

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            XACTIVIDADESTheme() {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PantallaMenu()
                }
            }
        }
    }
}

@Composable
fun PantallaMenu() {
    val contexto = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Menú")

        Button(
            onClick = {
                val intento = Intent(contexto, SaludoEstaticoActivity::class.java)
                contexto.startActivity(intento)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Saludo estático")
        }

        Button(
            onClick = {
                val intento = Intent(contexto, SaludoPersonalizadoActivity::class.java)
                contexto.startActivity(intento)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Saludo personalizado")
        }

        Button(
            onClick = {
                val intento = Intent(contexto, SumadoraActivity::class.java)
                contexto.startActivity(intento)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Sumadora")
        }

        Text(
            text = getAndroidId(contexto),
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}
