package com.example.botonesysonidos

import android.media.MediaPlayer
import android.os.Bundle
import android.content.Context
import android.content.res.Configuration
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.botonesysonidos.ui.theme.BOTONESYSONIDOSTheme

private data class Mob(
    val nombre: String,
    val imagen: Int,
    val sonido: Int
)

private val mobs = listOf(
    Mob("Creeper", R.drawable.mob_creeper, R.raw.creeper_explosion),
    Mob("Araña", R.drawable.mob_spider, R.raw.spider),
    Mob("Zombie", R.drawable.mob_zombie, R.raw.zombie),
    Mob("Esqueleto", R.drawable.mob_skeleton, R.raw.skeleton),
    Mob("Warden", R.drawable.mob_warden, R.raw.warden),
    Mob("Wither", R.drawable.mob_wither, R.raw.wither),
    Mob("Ender Dragon", R.drawable.mob_ender_dragon, R.raw.ender_dragon),
    Mob("Enderman", R.drawable.mob_enderman, R.raw.enderman),
    Mob("Ghast", R.drawable.mob_ghast, R.raw.ghast),
    Mob("Blaze", R.drawable.mob_blaze, R.raw.blaze),
    Mob("Aldeano", R.drawable.mob_aldeano, R.raw.aldeano),
    Mob("Cerdo", R.drawable.mob_cerdo, R.raw.cerdo)
)

// El ViewModel conserva el reproductor si Android recrea la Activity al girar.
class SonidosViewModel : ViewModel() {
    val reproductor = MediaPlayer()

    override fun onCleared() {
        reproductor.release()
        super.onCleared()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sonidosViewModel = ViewModelProvider(this).get(SonidosViewModel::class.java)

        setContent {
            BOTONESYSONIDOSTheme(darkTheme = false, dynamicColor = false) {
                Reticula(sonidosViewModel.reproductor)
            }
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
private fun Reticula(reproductor: MediaPlayer) {
    val contexto = LocalContext.current
    val orientacion = LocalConfiguration.current.orientation
    val columnas = if (orientacion == Configuration.ORIENTATION_LANDSCAPE) 4 else 3
    val filas = (mobs.size + columnas - 1) / columnas
    // remember reutiliza esta referencia entre recomposiciones; el ViewModel la mantiene al girar.
    val mediaPlayer = remember(reproductor) { reproductor }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, top = 28.dp, end = 8.dp, bottom = 8.dp),
    ) {
        Text(
            text = "MOBS Minecraft",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                for (fila in 0 until filas) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (columna in 0 until columnas) {
                            val indice = fila * columnas + columna

                            BoxWithConstraints(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                val tamanoBoton = minOf(maxWidth * 0.85f, maxHeight * 0.62f)

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    IconButton(
                                        onClick = {
                                            try {
                                                mediaPlayer.reset()
                                                contexto.resources.openRawResourceFd(mobs[indice].sonido).use { archivo ->
                                                    mediaPlayer.setDataSource(
                                                        archivo.fileDescriptor,
                                                        archivo.startOffset,
                                                        archivo.length
                                                    )
                                                }
                                                mediaPlayer.prepare()
                                                mediaPlayer.start()
                                            } catch (error: Exception) {
                                                Toast.makeText(
                                                    contexto,
                                                    "No se pudo reproducir el sonido",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        },
                                        modifier = Modifier.size(tamanoBoton)
                                    ) {
                                        Image(
                                            painter = painterResource(id = mobs[indice].imagen),
                                            contentDescription = mobs[indice].nombre,
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(3.dp)
                                        )
                                    }

                                    Text(
                                        text = mobs[indice].nombre,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = "UUID: ${getAndroidId(contexto)}",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )
    }
}
