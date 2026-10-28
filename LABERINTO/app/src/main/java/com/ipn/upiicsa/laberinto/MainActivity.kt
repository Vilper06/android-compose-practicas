package com.ipn.upiicsa.laberinto

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ipn.upiicsa.laberinto.ui.theme.LaberintoTheme

class MainActivity : ComponentActivity(), SensorEventListener {
    //variables para recordar valores de acelerometro
    private var ax by mutableFloatStateOf(0f)
    private var ay by mutableFloatStateOf(0f)

    //variables para registrar el acelerometro
    private lateinit var sensorManager: SensorManager
    private var acelerometro: Sensor? = null

    //sonidos (archivos en res/raw): SoundPool permite reproducir golpes rapidos sin retraso
    private lateinit var sonidos: SoundPool
    private var sonidoGolpe = 0
    private var sonidoLogro = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Obtener el acelerometro
        sensorManager = getSystemService(Context.SENSOR_SERVICE)
                        as SensorManager
        acelerometro = sensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER
        )

        sonidos = SoundPool.Builder().setMaxStreams(4).build()
        sonidoGolpe = sonidos.load(this, R.raw.golpe, 1)
        sonidoLogro = sonidos.load(this, R.raw.portal_end, 1)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            Juego(
                ax, ay,
                alChocar = { sonidos.play(sonidoGolpe, 1f, 1f, 1, 0, 1f) },
                alEscapar = { sonidos.play(sonidoLogro, 1f, 1f, 1, 0, 1f) }
            )
        }
    }

    //sobreecribir el estado para registrar el acelerometro
    override fun onResume() {
        super.onResume()
        acelerometro?.let {
            sensorManager.registerListener(this, it,
                SensorManager.SENSOR_DELAY_GAME)
        }
    }

    //sobreecribir el estado para liberar el acelerometro
    override fun onPause() {
        sensorManager.unregisterListener(this)
        ax = 0f
        ay = 0f
        super.onPause()

    }

    override fun onDestroy() {
        sonidos.release()
        super.onDestroy()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //ESTO NO LO NECESITO
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            ax = event.values[0]
            ay = event.values[1]
        }
    }
}
