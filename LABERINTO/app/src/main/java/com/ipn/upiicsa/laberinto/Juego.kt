package com.ipn.upiicsa.laberinto

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.min
import kotlin.math.sqrt

// Laberinto: 1 = pared, 0 = camino. Entrada arriba a la izquierda, salida abajo a la derecha.
private val MAPA = arrayOf(
    "1111111111111",
    "1000000010001",
    "1111111011101",
    "1000001000001",
    "1010111111101",
    "1010000010001",
    "1011101110111",
    "1010100000101",
    "1010111111101",
    "1000100010001",
    "1110101010101",
    "1000001000101",
    "1111111111101"
)
private const val SALIDA_COL = 11
private const val SALIDA_FIL = 12

private fun pared(c: Int, f: Int) =
    f !in MAPA.indices || c !in MAPA[0].indices || MAPA[f][c] == '1'

@Composable
fun Juego(ax: Float, ay: Float, alChocar: () -> Unit, alEscapar: () -> Unit) {
    val axActual by rememberUpdatedState(ax)
    val ayActual by rememberUpdatedState(ay)

    var px by remember { mutableFloatStateOf(1.5f) } // posicion relativa a la celda (en unidades de celda)
    var py by remember { mutableFloatStateOf(1.5f) }
    var escapo by remember { mutableStateOf(false) }

    val filas = MAPA.size
    val cols = MAPA[0].length
    val radio = 0.3f // en unidades de celda

    // Fisica en unidades de celda, independiente del tamaño de pantalla
    LaunchedEffect(Unit) {
        var vx = 0f
        var vy = 0f
        var anterior = 0L
        var ultimoGolpe = 0L
        while (true) {
            withFrameNanos { t ->
                val dt = if (anterior == 0L) 0f else ((t - anterior) / 1e9f).coerceAtMost(0.05f)
                anterior = t

                // El sensor acelera la pelota (mismo signo que en Acelerometro)
                vx = (vx - axActual * 6f * dt) * 0.97f
                vy = (vy + ayActual * 6f * dt) * 0.97f

                var x = px + vx * dt
                var y = py + vy * dt

                // Colision circulo vs cada celda pared cercana
                for (f in y.toInt() - 1..y.toInt() + 1) for (c in x.toInt() - 1..x.toInt() + 1) {
                    if (!pared(c, f)) continue
                    val cx = x.coerceIn(c.toFloat(), c + 1f)
                    val cy = y.coerceIn(f.toFloat(), f + 1f)
                    val dx = x - cx
                    val dy = y - cy
                    val d2 = dx * dx + dy * dy
                    if (d2 < radio * radio && d2 > 1e-8f) {
                        val d = sqrt(d2)
                        x = cx + dx / d * radio
                        y = cy + dy / d * radio
                        // quitar la velocidad que empuja hacia la pared
                        val dot = vx * dx / d + vy * dy / d
                        if (dot < 0f) {
                            vx -= dot * dx / d
                            vy -= dot * dy / d
                            // Sonido solo en golpes con cierta fuerza y con pausa entre golpes
                            if (-dot > 0.8f && t - ultimoGolpe > 250_000_000L) {
                                ultimoGolpe = t
                                alChocar()
                            }
                        }
                    }
                }
                px = x
                py = y

                if (x.toInt() == SALIDA_COL && y.toInt() == SALIDA_FIL) escapo = true
            }

            if (escapo) {
                alEscapar()
                delay(1200) // un momento para disfrutar el logro
                // Reinicio de nivel: de vuelta a la entrada, sin velocidad
                px = 1.5f
                py = 1.5f
                vx = 0f
                vy = 0f
                anterior = 0L
                escapo = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val celda = min(size.width / cols, size.height / filas)
            val ox = (size.width - celda * cols) / 2f
            val oy = (size.height - celda * filas) / 2f

            drawRect(color = Color(0xFF0A0A0A))

            // Muros de piedra (el suelo es el fondo negro)
            for (f in 0 until filas) for (c in 0 until cols) {
                val x0 = ox + c * celda
                val y0 = oy + f * celda
                if (MAPA[f][c] == '1') muroPiedra(x0, y0, celda)
            }

            portalDelEnd(ox + SALIDA_COL * celda, oy + SALIDA_FIL * celda, celda)

            // Pelota
            val centro = Offset(ox + px * celda, oy + py * celda)
            drawCircle(Color(0xFF0D4F44), radio * celda, centro)
        }
    }
}

// Ladrillo simple: un color gris plano con una sola linea de junta y un borde oscuro.
private fun DrawScope.muroPiedra(x0: Float, y0: Float, celda: Float) {
    drawRect(Color(0xFF7A7A7A), Offset(x0, y0), Size(celda, celda))
    val junta = Color(0xFF4A4A4A)
    val grosor = celda * 0.06f
    drawLine(junta, Offset(x0, y0 + celda / 2f), Offset(x0 + celda, y0 + celda / 2f), grosor)
    drawRect(junta, Offset(x0, y0), Size(celda, celda), style = Stroke(grosor))
}

// Portal al End: un solo color caracteristico (verde azulado del marco), sin detalles.
private fun DrawScope.portalDelEnd(x0: Float, y0: Float, celda: Float) {
    drawRect(Color(0xFF2F8F78), Offset(x0, y0), Size(celda, celda))
}
