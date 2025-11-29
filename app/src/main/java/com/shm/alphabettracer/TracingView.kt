package com.shm.alphabettracer

import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

class TracingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val pathPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 36f
        color = Color.parseColor("#FF7043")
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    private val path = Path()
    private lateinit var strokesBitmap: Bitmap
    private lateinit var strokesCanvas: Canvas

    private lateinit var letterBitmap: Bitmap
    private lateinit var letterCanvas: Canvas

    private var letterChar: Char = 'A'

    private var showError = false
    private val errorBgColor = Color.parseColor("#FFCDD2")

    var onTraceSuccess: (() -> Unit)? = null
    var onProgress: ((Float) -> Unit)? = null

    private val threshold = 0.60f
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    // NEW: block touch during TTS
    private var touchEnabled = true

    fun setTouchEnabled(enabled: Boolean) {
        touchEnabled = enabled
    }

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    fun setLetter(c: Char) {
        letterChar = c
        createLetterBitmap(width, height)
        invalidate()
    }

    private fun createLetterBitmap(w: Int, h: Int) {
        if (w == 0 || h == 0) return
        letterBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        letterCanvas = Canvas(letterBitmap)
        letterCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        val size = min(w, h) * 0.7f
        val paint = Paint().apply {
            color = Color.DKGRAY
            style = Paint.Style.STROKE
            strokeWidth = size / 12f
            textSize = size
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val fm = paint.fontMetrics
        val x = w / 2f
        val y = h / 2f - (fm.ascent + fm.descent) / 2f
        letterCanvas.drawText(letterChar.toString(), x, y, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        strokesBitmap = Bitmap.createBitmap(
            w.coerceAtLeast(1),
            h.coerceAtLeast(1),
            Bitmap.Config.ARGB_8888
        )
        strokesCanvas = Canvas(strokesBitmap)
        strokesCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        createLetterBitmap(w, h)
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        if (showError) canvas.drawColor(errorBgColor)
        else canvas.drawColor(Color.WHITE)

        canvas.drawBitmap(letterBitmap, 0f, 0f, null)
        canvas.drawBitmap(strokesBitmap, 0f, 0f, null)
        canvas.drawPath(path, pathPaint)
    }

    private var lastX = 0f
    private var lastY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {

        // NEW: block all touch when disabled
        if (!touchEnabled) return false

        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isInsideLetter(x, y)) {
                    path.moveTo(x, y)
                    lastX = x
                    lastY = y
                } else triggerErrorFeedback()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (isInsideLetter(x, y)) {
                    val dx = kotlin.math.abs(x - lastX)
                    val dy = kotlin.math.abs(y - lastY)
                    if (dx >= 2 || dy >= 2) {
                        path.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2)
                        lastX = x
                        lastY = y
                    }
                } else triggerErrorFeedback()
            }

            MotionEvent.ACTION_UP -> {
                if (isInsideLetter(x, y)) {
                    path.lineTo(x, y)
                    strokesCanvas.drawPath(path, pathPaint)
                }
                path.reset()
                post { evaluateTrace() }
            }
        }
        invalidate()
        return true
    }

    private fun isInsideLetter(x: Float, y: Float): Boolean {
        if (letterBitmap.width == 0 || letterBitmap.height == 0) return false

        val radius = (pathPaint.strokeWidth / 2).toInt()
        val startX = (x - radius).toInt().coerceIn(0, letterBitmap.width - 1)
        val endX = (x + radius).toInt().coerceIn(0, letterBitmap.width - 1)
        val startY = (y - radius).toInt().coerceIn(0, letterBitmap.height - 1)
        val endY = (y + radius).toInt().coerceIn(0, letterBitmap.height - 1)

        for (px in startX..endX) {
            for (py in startY..endY) {
                val pixel = letterBitmap.getPixel(px, py)
                val alpha = (pixel ushr 24) and 0xff
                if (alpha > 10) return true
            }
        }
        return false
    }

    private fun triggerErrorFeedback() {
        if (!showError) {
            showError = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(100)
            }

            postDelayed({
                showError = false
                invalidate()
            }, 500)

            invalidate()
        }
    }

    fun clearStrokes() {
        strokesCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        path.reset()
        invalidate()
    }

    fun hint() {
        strokesCanvas.drawBitmap(letterBitmap, 0f, 0f, null)
        postDelayed({
            strokesCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            invalidate()
        }, 700)
        invalidate()
    }

    private fun evaluateTrace() {
        val w = strokesBitmap.width
        val h = strokesBitmap.height
        if (w == 0 || h == 0) return

        val strokesPixels = IntArray(w * h)
        val letterPixels = IntArray(w * h)
        strokesBitmap.getPixels(strokesPixels, 0, w, 0, 0, w, h)
        letterBitmap.getPixels(letterPixels, 0, w, 0, 0, w, h)

        var letterCount = 0
        var hitCount = 0

        for (i in letterPixels.indices) {
            val lp = letterPixels[i]
            val alpha = (lp ushr 24) and 0xff
            if (alpha > 10) {
                letterCount++
                val sp = strokesPixels[i]
                val alph2 = (sp ushr 24) and 0xff
                if (alph2 > 10) hitCount++
            }
        }

        if (letterCount == 0) {
            onProgress?.invoke(0f)
            return
        }

        val ratio = hitCount.toFloat() / letterCount.toFloat()
        onProgress?.invoke(ratio)

        if (ratio >= threshold) {
            onTraceSuccess?.invoke()
        }
    }
}
