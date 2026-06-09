package com.example.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageUtils {

    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    fun base64ToBitmap(base64: String): Bitmap? {
        return try {
            val decodedString = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Generates a stunning synthetic linear/radial gradient, creating high-contrast landscape palettes.
     */
    fun generateGradientPlaceholder(type: String): String {
        val width = 500
        val height = 350
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()

        when (type) {
            "sunset" -> {
                // Sunset Orange to Purple Linear
                val shader = LinearGradient(
                    0f, 0f, 0f, height.toFloat(),
                    intArrayOf(Color.parseColor("#FF512F"), Color.parseColor("#DD2476"), Color.parseColor("#3F2B96")),
                    floatArrayOf(0f, 0.5f, 1f),
                    Shader.TileMode.CLAMP
                )
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

                // Draw a golden sun
                paint.shader = null
                paint.color = Color.parseColor("#FFE066")
                paint.isAntiAlias = true
                canvas.drawCircle(width / 2f, height * 0.6f, 50f, paint)

                // Draw dark silhouette hills
                paint.color = Color.parseColor("#1F1C2C")
                val hillPaint = Paint().apply {
                    color = Color.parseColor("#151324")
                    isAntiAlias = true
                }
                canvas.drawOval(-100f, height * 0.75f, width + 100f, height + 100f, hillPaint)
            }
            "cosmic" -> {
                // Nebula radial purple/blue
                val shader = RadialGradient(
                    width / 2f, height / 2f, 250f,
                    intArrayOf(Color.parseColor("#8E2DE2"), Color.parseColor("#4A00E0"), Color.parseColor("#0F2027")),
                    floatArrayOf(0f, 0.6f, 1f),
                    Shader.TileMode.CLAMP
                )
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

                // Add stars
                paint.shader = null
                paint.color = Color.WHITE
                paint.isAntiAlias = true
                val starPositions = listOf(
                    50f to 50f, 120f to 180f, 400f to 80f, 320f to 220f,
                    220f to 40f, 80f to 300f, 450f to 280f, 280f to 130f
                )
                for (pos in starPositions) {
                    canvas.drawCircle(pos.first, pos.second, 2f, paint)
                }
            }
            "ocean" -> {
                // Deep blue sea to turquoise
                val shader = LinearGradient(
                    0f, 0f, 0f, height.toFloat(),
                    intArrayOf(Color.parseColor("#00c6ff"), Color.parseColor("#0072ff"), Color.parseColor("#0A1E3F")),
                    null,
                    Shader.TileMode.CLAMP
                )
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

                // Some waves in light blue
                paint.shader = null
                paint.color = Color.parseColor("#40FFFFFF")
                paint.strokeWidth = 6f
                paint.style = Paint.Style.STROKE
                paint.isAntiAlias = true
                canvas.drawArc(100f, height * 0.5f, 250f, height * 0.6f, 180f, 180f, false, paint)
                canvas.drawArc(240f, height * 0.52f, 390f, height * 0.62f, 180f, 180f, false, paint)
            }
            else -> {
                // Aurora aurora-borealis themed green/black
                val shader = LinearGradient(
                    0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.parseColor("#0575E6"), Color.parseColor("#00F260"), Color.parseColor("#021B1A")),
                    null,
                    Shader.TileMode.CLAMP
                )
                paint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
        }

        return bitmapToBase64(bitmap)
    }
}
