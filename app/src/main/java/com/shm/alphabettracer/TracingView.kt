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
import kotlin.math.abs

class TracingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val pathPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 36f
        color = Color.parseColor("#FF7043") // Orange/Coral
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
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

    // --- TRACKING STATE ---
    private var activePointerId = MotionEvent.INVALID_POINTER_ID
    private var isDrawing = false
    private var lastX = 0f
    private var lastY = 0f

    // FIX: New flag to lock the view once success is triggered
    private var isTraceCompleted = false

    private var touchEnabled = true

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    fun setTouchEnabled(enabled: Boolean) {
        touchEnabled = enabled
        if (!enabled) {
            // Immediately kill any active trace
            resetTouchState()
            invalidate()
        }
    }

    fun setLetter(c: Char) {
        letterChar = c

        // 1. Reset the Success Lock so the new letter can be traced
        isTraceCompleted = false

        // Prevent crashes if view isn't measured yet
        if (width > 0 && height > 0) {
            createLetterBitmap(width, height)
            clearStrokes() // Clear previous drawings
        }

        // Strict reset of all touch data
        resetTouchState()
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
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            strokesBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            strokesCanvas = Canvas(strokesBitmap)
            strokesCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            createLetterBitmap(w, h)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (showError) canvas.drawColor(errorBgColor)
        else canvas.drawColor(Color.WHITE)

        if (::letterBitmap.isInitialized) canvas.drawBitmap(letterBitmap, 0f, 0f, null)
        if (::strokesBitmap.isInitialized) canvas.drawBitmap(strokesBitmap, 0f, 0f, null)

        canvas.drawPath(path, pathPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 1. If touch disabled OR user already finished this letter, BLOCK EVERYTHING.
        if (!touchEnabled || isTraceCompleted) return true

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                path.reset()

                val index = event.actionIndex
                val x = event.getX(index)
                val y = event.getY(index)

                if (isInsideLetter(x, y)) {
                    activePointerId = event.getPointerId(index)
                    startNewSegment(x, y)
                } else {
                    triggerErrorFeedback()
                }
                invalidate()
                return true
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (activePointerId == MotionEvent.INVALID_POINTER_ID) {
                    val index = event.actionIndex
                    val x = event.getX(index)
                    val y = event.getY(index)

                    if (isInsideLetter(x, y)) {
                        activePointerId = event.getPointerId(index)
                        startNewSegment(x, y)
                        invalidate()
                    }
                }
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (activePointerId == MotionEvent.INVALID_POINTER_ID) return true

                val index = event.findPointerIndex(activePointerId)
                if (index == -1) return true

                val x = event.getX(index)
                val y = event.getY(index)

                if (isInsideLetter(x, y)) {
                    if (!isDrawing) {
                        startNewSegment(x, y)
                    } else {
                        val dx = abs(x - lastX)
                        val dy = abs(y - lastY)
                        if (dx >= 4 || dy >= 4) {
                            path.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2)
                            lastX = x
                            lastY = y
                        }
                    }
                } else {
                    isDrawing = false
                    triggerErrorFeedback()
                }
                invalidate()
                return true
            }

            MotionEvent.ACTION_UP -> {
                handleFingerUp(event)
            }

            MotionEvent.ACTION_POINTER_UP -> {
                handleFingerUp(event)
            }

            MotionEvent.ACTION_CANCEL -> {
                resetTouchState()
                invalidate()
            }
        }
        return true
    }

    private fun startNewSegment(x: Float, y: Float) {
        path.moveTo(x, y)
        lastX = x
        lastY = y
        isDrawing = true
    }

    private fun handleFingerUp(event: MotionEvent) {
        val index = event.actionIndex
        val id = event.getPointerId(index)

        if (id == activePointerId) {
            val x = event.getX(index)
            val y = event.getY(index)

            if (isInsideLetter(x, y) && isDrawing) {
                path.lineTo(x, y)
                strokesCanvas.drawPath(path, pathPaint)
            }

            // Post evaluation
            post { evaluateTrace() }

            resetTouchState()
            invalidate()
        }
    }

    private fun resetTouchState() {
        activePointerId = MotionEvent.INVALID_POINTER_ID
        isDrawing = false
        path.reset()
    }

    private fun isInsideLetter(x: Float, y: Float): Boolean {
        if (!::letterBitmap.isInitialized || letterBitmap.width == 0) return false

        val radius = (pathPaint.strokeWidth / 2).toInt()
        val startX = (x - radius).toInt().coerceIn(0, letterBitmap.width - 1)
        val endX = (x + radius).toInt().coerceIn(0, letterBitmap.width - 1)
        val startY = (y - radius).toInt().coerceIn(0, letterBitmap.height - 1)
        val endY = (y + radius).toInt().coerceIn(0, letterBitmap.height - 1)

        for (px in startX..endX step 2) {
            for (py in startY..endY step 2) {
                val pixel = letterBitmap.getPixel(px, py)
                val alpha = (pixel ushr 24) and 0xff
                if (alpha > 10) return true
            }
        }
        return false
    }

    private fun triggerErrorFeedback() {
        // Don't show error if we are already done
        if (!showError && !isTraceCompleted) {
            showError = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
            postDelayed({
                showError = false
                invalidate()
            }, 300)
            invalidate()
        }
    }

    fun clearStrokes() {
        if (::strokesCanvas.isInitialized) {
            strokesCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            path.reset()
            invalidate()
        }
    }

    fun hint() {
        if (!::strokesCanvas.isInitialized) return
        strokesCanvas.drawBitmap(letterBitmap, 0f, 0f, null)
        postDelayed({
            if (::strokesCanvas.isInitialized) {
                strokesCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                invalidate()
            }
        }, 700)
        invalidate()
    }

    private fun evaluateTrace() {
        // Safety check: If already completed, ignore everything.
        if (isTraceCompleted) return

        if (!::strokesBitmap.isInitialized) return

        val w = strokesBitmap.width
        val h = strokesBitmap.height
        if (w == 0 || h == 0) return

        val strokesPixels = IntArray(w * h)
        val letterPixels = IntArray(w * h)
        strokesBitmap.getPixels(strokesPixels, 0, w, 0, 0, w, h)
        letterBitmap.getPixels(letterPixels, 0, w, 0, 0, w, h)

        var letterCount = 0
        var hitCount = 0

        for (i in letterPixels.indices step 4) {
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
            // SUCCESS!
            // 1. Lock the view immediately
            isTraceCompleted = true

            // 2. Disable touch internally immediately
            resetTouchState()

            // 3. Notify parent
            onTraceSuccess?.invoke()
        }
    }
}