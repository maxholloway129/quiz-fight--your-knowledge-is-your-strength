package com.example.ui.pixel

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

object PixelArtEngine {

    /**
     * Converts a character-based pixel art matrix into an ARGB_8888 ImageBitmap
     * suitable for nearest-neighbor (FilterQuality.None) rendering.
     */
    fun createSpriteBitmap(
        rows: List<String>,
        palette: Map<Char, Int>,
        flipHorizontal: Boolean = false
    ): ImageBitmap {
        val height = rows.size
        val width = rows.maxOfOrNull { it.length } ?: 1
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            val row = rows[y]
            for (x in 0 until width) {
                val ch = if (x < row.length) row[x] else '.'
                val argb = palette[ch] ?: 0x00000000
                val targetX = if (flipHorizontal) (width - 1 - x) else x
                pixels[y * width + targetX] = argb
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap.asImageBitmap()
    }
}
