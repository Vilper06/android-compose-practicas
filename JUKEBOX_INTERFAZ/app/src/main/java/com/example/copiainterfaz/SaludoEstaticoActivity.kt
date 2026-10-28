package com.example.copiainterfaz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.copiainterfaz.ui.theme.COPIAINTERFAZTheme

class SaludoEstaticoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            COPIAINTERFAZTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PantallaSaludoEstatico()
                }
            }
        }
    }
}

@Composable
fun PantallaSaludoEstatico() {
    val contexto = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "¡Hola, bienvenido!")

        Text(
            text = getAndroidId(contexto),
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}
