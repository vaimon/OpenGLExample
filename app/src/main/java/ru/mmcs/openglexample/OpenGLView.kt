package ru.mmcs.openglexample

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent

class OpenGLView(context: Context, attributes: AttributeSet) : GLSurfaceView(context, attributes) {
    private val renderer: GLRenderer
    private val TOUCH_SCALE_FACTOR: Float = 1f / 30f
    private var previousX: Float = 0f

    init{
        setEGLContextClientVersion(3)
        renderer = GLRenderer(context)
        setRenderer(renderer)
        // Use GLSurfaceView.RENDERMODE_CONTINUOUSLY if you want to implement something tick-based
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x: Float = event!!.x

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx: Float = x - previousX
                //  Handle screen swipe
                //  renderer.doSomething(dx * TOUCH_SCALE_FACTOR)
            }
            // Handle any other actions you need
        }
        previousX = x
        return true
    }
}