package com.example.copiainterfaz

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
import com.example.copiainterfaz.ui.theme.COPIAINTERFAZTheme

class SaludoPersonalizadoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            COPIAINTERFAZTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PantallaSaludoPersonalizado()
                }
            }
        }
    }
}

@Composable
fun PantallaSaludoPersonalizado() {
    var nombre by remember { mutableStateOf("") }
    var saludo by remember { mutableStateOf("") }
    val contexto = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { saludo = "Hola, $nombre!" },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Saludar")
        }

        Text(
            text = saludo,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = getAndroidId(contexto),
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}
