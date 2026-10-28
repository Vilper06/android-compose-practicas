# Prácticas de Android con Kotlin y Jetpack Compose

Ejercicios de la materia **Programación Móvil** (Ingeniería en Informática, IPN-UPIICSA, septiembre–octubre 2026). Cada carpeta es un proyecto de Android Studio independiente.

| Proyecto | Qué practica |
|---|---|
| `JUKEBOX_INTERFAZ` | Interfaz de reproductor con Compose: portada, vinilo, tarjetas de pista y controles; `MediaPlayer` con pistas cortas |
| `BOTONESYSONIDOS` | 12 botones con imagen que reproducen sonidos; `MediaPlayer` dentro de un `ViewModel` para sobrevivir al giro de pantalla |
| `Acelerometro` | Pelota que se mueve con el acelerómetro (`SensorManager`, `Canvas`) |
| `LABERINTO` | Juego de laberinto |
| `XACTIVIDADES` | Navegación entre `Activity` con `Intent`: menú, saludo estático, saludo personalizado y sumadora |
| `SUMADORA`, `CAJATEXTO`, `ENMEDIO`, `UUID` | Fundamentos de Compose: `TextField`, `Button`, estado con `remember`, `Column` y `Row` |

## Cómo abrirlos
Abre la carpeta del proyecto en Android Studio; el SDK se configura solo al sincronizar Gradle.

## Nota sobre recursos
Los sonidos de `BOTONESYSONIDOS` provenían de Minecraft (propiedad de Mojang Studios) y **no se incluyen** en este repositorio. Para ejecutar ese proyecto, agrega archivos `.ogg` propios en `app/src/main/res/raw` con los nombres listados en `ASSETS_CREDITS.txt`.
