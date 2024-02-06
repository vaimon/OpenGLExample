package ru.mmcs.openglexample

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import ru.mmcs.openglexample.objects.Object3D
import ru.mmcs.openglexample.objects.ObjectGL
import ru.mmcs.openglexample.objects.Triangle2D
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class GLRenderer(private val context: Context) : GLSurfaceView.Renderer {
    // Scene objects
    private val sceneObjects = mutableListOf<ObjectGL>()

    // Coordinate transform matrices
    private val vpMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)     // background color
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)           // depth buffer
        // Do any initialization you need here
//        sceneObjects.add(
//            Triangle2D(
//                readShaderFromAssets(context,"shaders/vertex/simple_shader.vert"),
//                readShaderFromAssets(context,"shaders/fragment/simple_shader.frag")
//            )
//        )
        sceneObjects.add(
            Object3D(
                context.assets.open("objects/teapot.obj"),
                floatArrayOf(1f, 1f, 0f, 1f),
                readShaderFromAssets(context,"shaders/vertex/color_shader.vert"),
                readShaderFromAssets(context,"shaders/fragment/color_shader.frag")
            )
        )
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        // Do whatever you need when surface size changes
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, 50f, ratio, 0.1f, 300f)
    }

    // Called on each frame redraw
    override fun onDrawFrame(p0: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        Matrix.setLookAtM(
            viewMatrix,
            0,
            WorldState.eyePosition[0],
            WorldState.eyePosition[1],
            WorldState.eyePosition[2],
            0f,
            0f,
            0f,
            0f,
            1.0f,
            0.0f
        )
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // Draw everything in the scene
        for (shape in sceneObjects) {
            shape.draw(vpMatrix)
        }
    }

    companion object {
        fun compileShader(type: Int, shaderCode: String): Int {
            val id = GLES30.glCreateShader(type).also { shader ->
                GLES30.glShaderSource(shader, shaderCode)
                GLES30.glCompileShader(shader)
            }

            val compileStatus = IntArray(1)
            GLES30.glGetShaderiv(id, GLES30.GL_COMPILE_STATUS, compileStatus, 0)
            if (compileStatus[0] == 0) {
                Log.e("GL_DEBUG", "Shader compilation error: " + GLES30.glGetShaderInfoLog(id))
            }

            return id
        }

        fun checkGLError(tag: String = "") {
            val err = GLES30.glGetError()
            if (err != GLES30.GL_NO_ERROR) {
                Log.e("GL_DEBUG", "OpenGL Error: $err [$tag]")
            }
        }

        // Asset path must be like: "shaders/vertex/shader.vert"
        fun readShaderFromAssets(context: Context, assetPath: String): String {
            return context.assets
                .open(assetPath)
                .bufferedReader().use {
                    it.readText()
                }
        }
    }
}