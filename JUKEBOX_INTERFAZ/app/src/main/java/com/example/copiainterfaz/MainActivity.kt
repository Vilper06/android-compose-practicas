package com.example.copiainterfaz

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.verticalScroll
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.copiainterfaz.ui.theme.COPIAINTERFAZTheme

private val Fondo = Color(0xFF111323)
private val Panel = Color(0xFF1D2035)
private val Texto = Color(0xFFF7F2E9)
private val TextoSecundario = Color(0xFFB4B7CB)
private val Amarillo = Color(0xFFFFD166)

private data class Pista(
    val titulo: String,
    val estilo: String,
    val simbolo: String,
    val color: Color,
    val archivo: Int
)

// Cada pista apunta a un audio guardado en app/src/main/res/raw.
private val pistas = listOf(
    Pista("Insert Coin", "CHIPTUNE · 8 s", "＋", Color(0xFFFF6B6B), R.raw.insert_coin),
    Pista("Neón en la lluvia", "SYNTH · 8 s", "✦", Color(0xFF8B7CFF), R.raw.neon),
    Pista("Carrera nocturna", "ARCADE · 8 s", "➤", Color(0xFF42C6A5), R.raw.carrera),
    Pista("Nivel secreto", "CHIPTUNE · 8 s", "★", Color(0xFFFFA552), R.raw.nivel_secreto),
    Pista("Último continue", "SYNTH · 8 s", "↻", Color(0xFF60B8FF), R.raw.ultimo_continue),
    Pista("Créditos al amanecer", "ARCADE · 8 s", "☼", Color(0xFFE685B5), R.raw.creditos)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.rgb(17, 19, 35)),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.rgb(17, 19, 35))
        )
        setContent {
            COPIAINTERFAZTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Fondo) {
                    PantallaJukebox()
                }
            }
        }
    }
}

@Composable
fun PantallaJukebox() {
    val contexto = LocalContext.current
    // remember conserva el reproductor entre recomposiciones de Compose.
    val reproductor = remember { MediaPlayer() }
    var pistaActual by remember { mutableStateOf(0) }
    var reproduciendo by remember { mutableStateOf(false) }
    var estado by remember { mutableStateOf("LISTA PARA SONAR") }

    // El reproductor se libera al salir de la pantalla.
    DisposableEffect(reproductor) {
        reproductor.setOnCompletionListener {
            reproduciendo = false
            estado = "PISTA FINALIZADA"
        }
        onDispose {
            reproductor.release()
        }
    }

    // Reinicia el mismo MediaPlayer y carga el audio elegido desde res/raw.
    val reproducirPista: (Int) -> Unit = { indice ->
        try {
            val pista = pistas[indice]
            reproductor.reset()
            contexto.resources.openRawResourceFd(pista.archivo).use { archivo ->
                reproductor.setDataSource(
                    archivo.fileDescriptor,
                    archivo.startOffset,
                    archivo.length
                )
            }
            reproductor.prepare()
            reproductor.start()
            pistaActual = indice
            reproduciendo = true
            estado = "REPRODUCIENDO"
        } catch (error: Exception) {
            reproduciendo = false
            estado = "NO SE PUDO CARGAR"
            Toast.makeText(contexto, "No se pudo reproducir esta pista", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Encabezado()

            Portada(pista = pistas[pistaActual], estado = estado)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BotonReproductor("|◀", "Anterior") {
                    val anterior = (pistaActual - 1 + pistas.size) % pistas.size
                    reproducirPista(anterior)
                }
                BotonReproductor(
                    simbolo = if (reproduciendo) "Ⅱ" else "▶",
                    etiqueta = if (reproduciendo) "Pausar" else "Reproducir",
                    principal = true
                ) {
                    if (reproduciendo) {
                        reproductor.pause()
                        reproduciendo = false
                        estado = "EN PAUSA"
                    } else if (estado == "EN PAUSA") {
                        reproductor.start()
                        reproduciendo = true
                        estado = "REPRODUCIENDO"
                    } else {
                        reproducirPista(pistaActual)
                    }
                }
                BotonReproductor("■", "Detener") {
                    reproductor.reset()
                    pistaActual = 0
                    reproduciendo = false
                    estado = "LISTA PARA SONAR"
                }
                BotonReproductor("▶|", "Siguiente") {
                    val siguiente = (pistaActual + 1) % pistas.size
                    reproducirPista(siguiente)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "ELIGE TU PISTA",
                    color = Texto,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Seis melodías originales con sabor a arcade.",
                    color = TextoSecundario,
                    fontSize = 13.sp
                )
            }

            // El paso de dos crea filas de dos tarjetas y comprueba la última pista.
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (indice in pistas.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TarjetaPista(
                            pista = pistas[indice],
                            seleccionada = indice == pistaActual,
                            modifier = Modifier.weight(1f)
                        ) { reproducirPista(indice) }

                        if (indice + 1 < pistas.size) {
                            TarjetaPista(
                                pista = pistas[indice + 1],
                                seleccionada = indice + 1 == pistaActual,
                                modifier = Modifier.weight(1f)
                            ) { reproducirPista(indice + 1) }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Text(
                text = "PIXEL JUKEBOX  ·  HECHA PARA VOLVER A JUGAR",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 8.dp),
                color = TextoSecundario,
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun Encabezado() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "RADIO PIXEL",
                color = Amarillo,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = "Jukebox",
                color = Texto,
                fontSize = 29.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Text(
            text = "●  8 BITS",
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF292B44))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            color = Color(0xFF88E0C5),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun Portada(pista: Pista, estado: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(Panel)
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(26.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Vinilo(colorCentro = pista.color)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "SELECCIÓN ACTUAL",
                color = Amarillo,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = pista.titulo,
                modifier = Modifier.padding(top = 7.dp),
                color = Texto,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = pista.estilo,
                modifier = Modifier.padding(top = 5.dp),
                color = TextoSecundario,
                fontSize = 9.sp,
                letterSpacing = 0.5.sp
            )
            Row(
                modifier = Modifier.padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (estado == "REPRODUCIENDO") Amarillo else TextoSecundario)
                )
                Text(
                    text = estado,
                    modifier = Modifier.padding(start = 6.dp),
                    color = TextoSecundario,
                    fontSize = 9.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun Vinilo(colorCentro: Color) {
    Box(
        modifier = Modifier
            .size(116.dp)
            .clip(CircleShape)
            .background(Color(0xFF131426))
            .border(1.dp, Color.White.copy(alpha = 0.14f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier.size(96.dp).border(1.dp, Color(0xFF41435D), CircleShape))
        Box(Modifier.size(76.dp).border(1.dp, Color(0xFF34364F), CircleShape))
        Box(Modifier.size(55.dp).border(1.dp, Color(0xFF41435D), CircleShape))
        Box(
            modifier = Modifier
                .size(35.dp)
                .clip(CircleShape)
                .background(colorCentro),
            contentAlignment = Alignment.Center
        ) {
            Text("♪", color = Fondo, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BotonReproductor(
    simbolo: String,
    etiqueta: String,
    principal: Boolean = false,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .size(if (principal) 58.dp else 46.dp)
                .semantics { contentDescription = etiqueta },
            shape = CircleShape,
            border = if (principal) null else BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (principal) Amarillo else Color(0xFF292B44),
                contentColor = if (principal) Fondo else Texto
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
            Text(
                text = simbolo,
                fontSize = if (principal) 21.sp else 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = etiqueta,
            modifier = Modifier.padding(top = 5.dp),
            color = TextoSecundario,
            fontSize = 9.sp
        )
    }
}

@Composable
private fun TarjetaPista(
    pista: Pista,
    seleccionada: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (seleccionada) Color(0xFF302E4A) else Panel)
            .border(
                width = 1.dp,
                color = if (seleccionada) pista.color.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.06f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(pista.color),
            contentAlignment = Alignment.Center
        ) {
            Text(pista.simbolo, color = Fondo, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 9.dp)
        ) {
            Text(
                text = pista.titulo,
                color = Texto,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (seleccionada) "SELECCIONADA" else pista.estilo,
                modifier = Modifier.padding(top = 3.dp),
                color = if (seleccionada) pista.color else TextoSecundario,
                fontSize = 8.sp,
                letterSpacing = 0.3.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
