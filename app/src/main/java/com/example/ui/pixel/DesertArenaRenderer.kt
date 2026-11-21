package com.example.ui.pixel

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Generates a static, coherent 16-bit retro pixel-art desert fighting arena (240x136)
 * with sunset sky dithering, stepped sandstone mesas, canyon cliffs, cacti, and desert floor.
 */
object DesertArenaRenderer {

    const val ARENA_WIDTH = 240
    const val ARENA_HEIGHT = 136
    const val HORIZON_Y = 92

    val arenaBitmap: ImageBitmap by lazy { buildDesertArenaBitmap() }

    private fun buildDesertArenaBitmap(): ImageBitmap {
        val w = ARENA_WIDTH
        val h = ARENA_HEIGHT
        val pixels = IntArray(w * h)

        val skyPalette = intArrayOf(
            0xFF231645.toInt(),
            0xFF3E235C.toInt(),
            0xFF6B3267.toInt(),
            0xFF9F425E.toInt(),
            0xFFCE5F47.toInt(),
            0xFFEB8944.toInt(),
            0xFFF7BC5C.toInt()
        )

        // 1. Dithered pixel sky
        for (y in 0 until HORIZON_Y) {
            val norm = y.toFloat() / (HORIZON_Y - 1)
            val scaled = norm * (skyPalette.size - 1)
            val idx = scaled.toInt().coerceIn(0, skyPalette.size - 2)
            val frac = scaled - idx
            for (x in 0 until w) {
                val bayer = ((x and 1) xor ((y and 1) shl 1)) / 4f + 0.12f
                val pick = if (frac > bayer) idx + 1 else idx
                pixels[y * w + x] = skyPalette[pick]
            }
        }

        // 2. Retro sunset sun disc near center-left horizon
        val sunCx = 120
        val sunCy = 66
        val sunRadius = 18
        for (y in (sunCy - sunRadius)..(sunCy + sunRadius)) {
            if (y !in 0 until HORIZON_Y) continue
            for (x in (sunCx - sunRadius)..(sunCx + sunRadius)) {
                if (x !in 0 until w) continue
                val dx = x - sunCx
                val dy = y - sunCy
                val distSq = dx * dx + dy * dy
                if (distSq <= sunRadius * sunRadius) {
                    // Horizontal retro scanline strips inside lower sun
                    val isStripe = y > sunCy + 4 && (y % 3 == 0)
                    if (!isStripe) {
                        pixels[y * w + x] = if (distSq < 110) 0xFFFFE58A.toInt() else 0xFFFFC14D.toInt()
                    }
                }
            }
        }

        // 3. Distant stepped desert mountain & mesa ridge
        val farMesaCol = 0xFF693556.toInt()
        val farMesaHighlight = 0xFF84435E.toInt()
        for (x in 0 until w) {
            val ridgeY = when {
                x in 14..56 -> 52 + ((x - 35) * (x - 35)) / 42
                x in 76..108 -> 62 + kotlin.math.abs(x - 92) / 3
                x in 146..196 -> 48 + if (x in 156..184) 0 else kotlin.math.abs(x - 170) / 2
                x in 204..234 -> 58 + kotlin.math.abs(x - 219) / 2
                else -> 72 + ((x * 7) % 5)
            }.coerceAtMost(HORIZON_Y - 1)

            for (y in ridgeY until HORIZON_Y) {
                pixels[y * w + x] = if (y <= ridgeY + 1) farMesaHighlight else farMesaCol
            }
        }

        // 4. Mid-ground canyon rock formations & dunes
        val nearMesaCol = 0xFF96453E.toInt()
        val nearMesaShade = 0xFF733032.toInt()
        val nearMesaRim = 0xFFBF6046.toInt()
        for (x in 0 until w) {
            val rockY = when {
                x in 0..34 -> 70 + x / 3
                x in 46..72 -> 77 + kotlin.math.abs(x - 59) / 3
                x in 176..210 -> 74 + kotlin.math.abs(x - 193) / 3
                x in 212..239 -> 68 + (239 - x) / 3
                else -> 84 + ((x / 6) % 3)
            }.coerceAtMost(HORIZON_Y)

            for (y in rockY until HORIZON_Y) {
                pixels[y * w + x] = when {
                    y == rockY -> nearMesaRim
                    x % 11 == 0 && y > rockY + 3 -> nearMesaShade
                    else -> nearMesaCol
                }
            }
        }

        // 5. Silhouetted desert cacti on far left & right edges (outside fighter area)
        drawCactus(pixels, w, baseX = 12, baseY = HORIZON_Y, height = 18, color = 0xFF422638.toInt())
        drawCactus(pixels, w, baseX = 224, baseY = HORIZON_Y, height = 21, color = 0xFF422638.toInt())

        // 6. Desert sand ground with pixel bands, dunes & pebbles
        val sandHighlight = 0xFFF0C27B.toInt()
        val sandMain = 0xFFD99B56.toInt()
        val sandMid = 0xFFBA783C.toInt()
        val sandDark = 0xFF8F522B.toInt()

        for (y in HORIZON_Y until h) {
            val depth = y - HORIZON_Y
            for (x in 0 until w) {
                val baseColor = when {
                    depth == 0 -> sandDark
                    depth in 1..2 -> sandHighlight
                    depth < 14 -> {
                        if (((x + y * 2) % 13 == 0)) sandHighlight else sandMain
                    }
                    depth < 30 -> {
                        if (((x * 3 + y) % 17 == 0)) sandMid else sandMain
                    }
                    else -> {
                        if (((x + y) % 5 == 0)) sandDark else sandMid
                    }
                }
                pixels[y * w + x] = baseColor
            }
        }

        // 7. Scattered foreground desert stones & arena boundary markers
        val stoneCoords = listOf(18 to 108, 42 to 124, 196 to 112, 218 to 127, 114 to 129)
        for ((sx, sy) in stoneCoords) {
            if (sx + 4 < w && sy + 2 < h) {
                pixels[sy * w + sx] = 0xFF7D4527.toInt()
                pixels[sy * w + sx + 1] = 0xFFAB673A.toInt()
                pixels[sy * w + sx + 2] = 0xFF7D4527.toInt()
                pixels[(sy + 1) * w + sx + 1] = 0xFF5A2F1A.toInt()
                pixels[(sy + 1) * w + sx + 2] = 0xFF5A2F1A.toInt()
            }
        }

        // 8. Subtle arena center tile markers on sand floor
        for (x in 52..188 step 12) {
            pixels[(HORIZON_Y + 24) * w + x] = sandHighlight
            pixels[(HORIZON_Y + 24) * w + x + 1] = sandHighlight
        }

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
        return bitmap.asImageBitmap()
    }

    private fun drawCactus(
        pixels: IntArray,
        width: Int,
        baseX: Int,
        baseY: Int,
        height: Int,
        color: Int
    ) {
        for (dy in 0 until height) {
            val y = baseY - dy
            if (y < 0) continue
            for (dx in -1..1) {
                val x = baseX + dx
                if (x in 0 until width) pixels[y * width + x] = color
            }
        }
        // Left arm
        val armY = baseY - (height * 2 / 3)
        for (dx in -5..-2) {
            val x = baseX + dx
            if (x in 0 until width && armY in 0 until ARENA_HEIGHT) pixels[armY * width + x] = color
        }
        for (dy in 0..5) {
            val x = baseX - 5
            val y = armY - dy
            if (x in 0 until width && y in 0 until ARENA_HEIGHT) pixels[y * width + x] = color
        }
        // Right arm
        val armRY = baseY - (height / 2)
        for (dx in 2..5) {
            val x = baseX + dx
            if (x in 0 until width && armRY in 0 until ARENA_HEIGHT) pixels[armRY * width + x] = color
        }
        for (dy in 0..4) {
            val x = baseX + 5
            val y = armRY - dy
            if (x in 0 until width && y in 0 until ARENA_HEIGHT) pixels[y * width + x] = color
        }
    }
}
